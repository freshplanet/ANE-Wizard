package com.freshplanet.ane.wizards.flashbuilder;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.operation.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import java.io.*;

import org.eclipse.ui.*;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;


public class NewANEProjectWizard extends Wizard implements INewWizard
{
	private WizardNewProjectCreationPage wizardPage;
	private IConfigurationElement config;
	private IWorkbench workbench;
	private IStructuredSelection selection;
	private IProject project;

	/**
	 * Constructor
	 */
	public NewANEProjectWizard()
	{
		super();
	}
	
	/**
	 * Adding the page to the wizard.
	 */
	public void addPages() 
	{
		wizardPage = new WizardNewProjectCreationPage("NewANEProject");
		wizardPage.setDescription("Create a new ANE Project");
		wizardPage.setTitle("New ANE Project");
		addPage(wizardPage);
	}

	/**
	 * This method is called when 'Finish' button is pressed in
	 * the wizard. We will create an operation and run it
	 * using wizard as execution context.
	 */
	@Override
	public boolean performFinish()
	{
		if (project != null) return true;
		
		final IProject projectHandle = wizardPage.getProjectHandle();
		URI projectURI = (!wizardPage.useDefaults()) ? wizardPage.getLocationURI() : null;
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IProjectDescription desc = workspace.newProjectDescription(projectHandle.getName());
		desc.setLocationURI(projectURI);
		
		WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
            protected void execute(IProgressMonitor monitor) throws CoreException 
            {
                createProject(desc, projectHandle, monitor);
            }
        };
        
        try 
        {
            getContainer().run(true, true, op);
        } 
        catch (InterruptedException e)
        {
            return false;
        } 
        catch (InvocationTargetException e) 
        {
            Throwable realException = e.getTargetException();
            MessageDialog.openError(getShell(), "Error", realException.getMessage());
            return false;
        }
        
        project = projectHandle;

        if (project == null) return false;
        
        BasicNewProjectResourceWizard.updatePerspective(config);
        BasicNewProjectResourceWizard.selectAndReveal(project, workbench.getActiveWorkbenchWindow());

        return true;
	}
	
	/**
     * This creates the project in the workspace.
     * 
     * @param description
     * @param projectHandle
     * @param monitor
     * @throws CoreException
     * @throws OperationCanceledException
     */
    void createProject(IProjectDescription description, IProject proj, IProgressMonitor monitor) throws CoreException, OperationCanceledException 
	{
        try 
        {
            monitor.beginTask("", 2000);
            
            proj.create(description, new SubProgressMonitor(monitor, 1000));
            
            if (monitor.isCanceled()) throw new OperationCanceledException();
            
            proj.open(IResource.BACKGROUND_REFRESH, new SubProgressMonitor(monitor, 1000));
            
            /*
             * Okay, now we have the project and we can do more things with it
             * before updating the perspective.
             */
            IContainer container = (IContainer) proj;
            
            // Create folders
            createFolder("src", container, monitor);
            createFolder("src/com", container, monitor);
            createFolder("src/com/freshplanet", container, monitor);
            createFolder("src/com/freshplanet/ane", container, monitor);

            // Copy files
            String projectName = description.getName();
            copyFile(".project", ".project", projectName, container, monitor);
            copyFile(".actionScriptProperties", ".actionScriptProperties", projectName, container, monitor);
            copyFile(".flexLibProperties", ".flexLibProperties", projectName, container, monitor);
            copyFile("extension.xml", "src/extension.xml", projectName, container, monitor);
            copyFile("Main.as", "src/com/freshplanet/ane/"+ projectName + ".as", projectName, container, monitor);
        }
        catch (IOException ioe) 
        {
			IStatus status = new Status(IStatus.ERROR, "NewFileWizard", IStatus.OK,
					ioe.getLocalizedMessage(), null);
			throw new CoreException(status);
		}
        finally
        {
            monitor.done();
        }
    }
    
    private void createFolder(String path, IContainer container, IProgressMonitor monitor) throws CoreException
    {
    	final IFolder folder = container.getFolder(new Path(path));
		folder.create(true, true, monitor);
    }
    
    private void copyFile(String resourceName, String finalPath, String projectName, IContainer container, IProgressMonitor monitor) throws IOException, CoreException
    {
    	InputStream resourceStream = openFilteredResource(resourceName, projectName);
        addFileToProject(container, new Path(finalPath), resourceStream, monitor);
        resourceStream.close();
    }
    
    /**
     * Adds a new file to the project.
     * 
     * @param container
     * @param path
     * @param contentStream
     * @param monitor
     * @throws CoreException
     */
    private void addFileToProject(IContainer container, Path path, InputStream contentStream, IProgressMonitor monitor) throws CoreException 
	{
        final IFile file = container.getFile(path);

        if (file.exists()) 
        {
            file.setContents(contentStream, true, true, monitor);
        } 
        else 
        {
            file.create(contentStream, true, monitor);
        }

    }
    
    private InputStream openFilteredResource(String resourceName, String projectName) throws CoreException
    {
    	final String newline = "\n";
        String line;
        StringBuffer sb = new StringBuffer();
        try 
        {
            InputStream input = this.getClass().getResourceAsStream("resources/" + resourceName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            try {

                while ((line = reader.readLine()) != null) {
                    line = line.replaceAll("\\$\\{PROJECT_NAME\\}", projectName);
                    sb.append(line);
                    sb.append(newline);
                }

            } finally {
                reader.close();
            }

        } 
        catch (IOException ioe) 
        {
            IStatus status = new Status(IStatus.ERROR, "ExampleWizard", IStatus.OK, ioe.getLocalizedMessage(), null);
            throw new CoreException(status);
        }

        return new ByteArrayInputStream(sb.toString().getBytes());
    }
    
	/**
	 * We will accept the selection in the workbench to see if
	 * we can initialize from it.
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) 
	{
		this.workbench = workbench;
		this.selection = selection;
	}
	
	/**
	 * Sets the initialization data for the wizard.
	 */
	public void setInitializationData(IConfigurationElement config,
			String propertyName, Object data) throws CoreException 
	{
		this.config = config;
	}
}