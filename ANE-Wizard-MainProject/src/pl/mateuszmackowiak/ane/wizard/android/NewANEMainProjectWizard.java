package pl.mateuszmackowiak.ane.wizard.android;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.operation.*;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import java.io.*;

import javax.swing.JOptionPane;

import org.eclipse.ui.*;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;



public class NewANEMainProjectWizard extends Wizard implements INewWizard
{
	private WizardNewProjectWithAirSDKLocationPage wizardPage;
	private IConfigurationElement config;
	private IWorkbench workbench;
	private IStructuredSelection selection;
	private IProject project;

	/**
	 * Constructor
	 */
	public NewANEMainProjectWizard()
	{
		super();
	}
	
	
	
	
	/**
	 * Adding the page to the wizard.
	 */
	public void addPages() 
	{
		wizardPage = new WizardNewProjectWithAirSDKLocationPage("NewANEMainProject");
		wizardPage.setDescription("Create a new ANE Main Project");
		wizardPage.setTitle("New ANE Main Project");
		wizardPage.setInitialPackageName("com.yourcompany");
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
		final String AirSDKLocation = wizardPage.getSDKLocationName();
		final String packageName = wizardPage.getPackageName();
		final URI projectURI = (!wizardPage.useDefaults()) ? wizardPage.getLocationURI() : null;
		final IProjectDescription desc = ResourcesPlugin.getWorkspace().newProjectDescription(projectHandle.getName());
		desc.setLocationURI(projectURI);
		
		try 
        {
	        try{
	        	System.setProperty(WizardNewProjectWithAirSDKLocationPage.aneAIRSDKPathKey, AirSDKLocation);
	        }catch(Exception e){}
        
        
            getContainer().run(true, true, new WorkspaceModifyOperation() {
                protected void execute(IProgressMonitor monitor) throws CoreException 
                {
                    createProject(desc, projectHandle, monitor,AirSDKLocation, packageName);
                }
            });
            
            
            getContainer().run(true, true, new WorkspaceModifyOperation() {
                protected void execute(IProgressMonitor monitor) throws CoreException 
                {
                	try 
                    {
                		String projectName = projectHandle.getName();
    	            	final IProjectDescription descript = ResourcesPlugin.getWorkspace().newProjectDescription(projectName+"_AS");
    	            	if(projectURI!=null){
    	            		descript.setLocationURI( new URI(projectURI.toURL().toString()+"/AS"));
    	            	}else{
    	            		descript.setLocationURI( new URI((ResourcesPlugin.getWorkspace().getRoot().getLocation().toString()+"/"+projectHandle.getName()+"/AS").replace(" ", "%20")));
    	            	}
    	            	
    	            	IProject proj = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName+"_AS");
    	            	if(proj==null){
    	            		throw new Exception("proj null");
    	            	}
                        monitor.beginTask("", 2000);
                        proj.create(descript, new SubProgressMonitor(monitor, 1000));
                        if (monitor.isCanceled()) throw new OperationCanceledException();
                        proj.open(IResource.BACKGROUND_REFRESH, new SubProgressMonitor(monitor, 1000));
                        
                        createASProject(projectName, packageName, monitor, (IContainer) proj,AirSDKLocation);
                    }
                    catch (Exception ioe) 
                    {
            			IStatus status = new Status(IStatus.ERROR, "NewFileWizard", IStatus.OK,
            					ioe.toString(), null);
            			throw new CoreException(status);
            		}
                    finally
                    {
                        monitor.done();
                    }
                }
            });
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
        } catch (Exception e1) {
        	MessageDialog.openError(getShell(), "Error", e1.toString());
        	return false;
		}
        
        project = projectHandle;

        if (project == null) return false;
        
        BasicNewProjectResourceWizard.updatePerspective(config);
        BasicNewProjectResourceWizard.selectAndReveal(project, workbench.getActiveWorkbenchWindow());

        return true;
	}
	
	void createASProject(String projectName,String packageName, IProgressMonitor monitor, IContainer container,String AirSDKLocation) throws CoreException, IOException{
		
		
		createFolder("src", container, monitor);
		createFolder("bin", container, monitor);
		createFolder("libs", container, monitor);
		
		 String fullPackageString = "src";
         //Create folders if package not empty
         if(packageName!=null && !packageName.isEmpty()){
         	String[] packages = packageName.split("\\.");
         	
         	for (String packageElement : packages) {
         		fullPackageString+="/"+packageElement;
         		createFolder(fullPackageString, container, monitor);
				}   
         }
         copyFile(".project", ".project", projectName,packageName,AirSDKLocation, container, monitor);
         copyFile(".actionScriptProperties", ".actionScriptProperties", projectName,packageName,AirSDKLocation, container, monitor);
         copyFile(".flexLibProperties", ".flexLibProperties", projectName,packageName,AirSDKLocation, container, monitor);
        
         
         copyFile("Main.as", fullPackageString+"/"+ projectName + ".as", projectName,packageName,AirSDKLocation, container, monitor);
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
    void createProject(IProjectDescription description, IProject proj, IProgressMonitor monitor,String AirSDKLocation ,String packageName) throws CoreException, OperationCanceledException 
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
            createFolder("AS", container, monitor);
            createFolder("NativeAndroid", container, monitor);
            createFolder("NativeIOS", container, monitor);

            
            // Copy files
            String projectName = description.getName();
            copyFile("build.properties.resources", "build.properties", projectName,packageName,AirSDKLocation, container, monitor);
            copyFile("build.xml.resources", "build.xml", projectName,packageName,AirSDKLocation, container, monitor);
            copyFile("extension.xml", "extension.xml", projectName,packageName,AirSDKLocation, container, monitor);
            
            try{
	            description.setName(projectName+"_AS");
	            
	            description.setLocation(new Path(container.getProjectRelativePath()+"/AS"));
	            proj.create(description, new SubProgressMonitor(monitor, 1000));
            }catch(Exception e){
            	System.out.println(e.toString());
            }
        }
        catch (IOException ioe) 
        {
			IStatus status = new Status(IStatus.ERROR, "NewFileWizard", IStatus.OK,
					ioe.toString(), null);
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
    
    private void copyFile(String resourceName, String finalPath, String projectName,String packageName, String AirSDKLocation, IContainer container, IProgressMonitor monitor) throws IOException, CoreException
    {
    	InputStream resourceStream = openFilteredResource(resourceName, projectName,packageName,AirSDKLocation);
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
    
    private InputStream openFilteredResource(String resourceName, String projectName, String packageName,String AirSDKLocation) throws CoreException
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
                	if(packageName!=null && !packageName.isEmpty())
                		line = line.replaceAll("\\$\\{MYPACKAGE_NAME\\}", packageName);
                	else{
                		line = line.replaceAll("\\$\\{MYPACKAGE_NAME\\}", "");
                	}
                	line = line.replaceAll("\\$\\{MYAIR_SDK_PATH\\}", AirSDKLocation);
                    line = line.replaceAll("\\$\\{MYPROJECT_NAME\\}", projectName);
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