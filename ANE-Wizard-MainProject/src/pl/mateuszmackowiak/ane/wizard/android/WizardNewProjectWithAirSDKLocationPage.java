package pl.mateuszmackowiak.ane.wizard.android;


import java.io.File;
import java.net.URI;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.WorkingSetGroup;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.internal.ide.IIDEHelpContextIds;
import org.eclipse.ui.internal.ide.dialogs.ProjectContentsLocationArea;
import org.eclipse.ui.internal.ide.dialogs.ProjectContentsLocationArea.IErrorMessageReporter;


/**
 * 
 * @author Mateusz Mackowiak
 *	
 */
public class WizardNewProjectWithAirSDKLocationPage extends WizardPage {

	public static boolean isMac(){
		return System.getProperty("os.name").toLowerCase().indexOf("mac") >= 0;
	}
	public static boolean isWindows(){
		return System.getProperty("os.name").toLowerCase().indexOf("win") >= 0;
	}
	
	   // initial value stores
    private String initialProjectFieldValue;
    private String initialPackageFieldValue;
    
    // widgets
    Text projectNameField;
    Text sdkLocationField;
    Text packageNameField;
    
    private Listener sdkLocatioModifyListener = new Listener() {
		
		@Override
		public void handleEvent(Event event) {
			boolean valid = validatePage();
            setPageComplete(valid);
		}
	};
	private Listener packageModifyListener = new Listener() {
		
		@Override
		public void handleEvent(Event event) {
			boolean valid = validatePage();
            setPageComplete(valid);
		}
	};
    private Listener nameModifyListener = new Listener() {
        public void handleEvent(Event e) {
            setLocationForSelection();
            boolean valid = validatePage();
            setPageComplete(valid);
                
        }
    };

    private ProjectContentsLocationArea locationArea;
    
    
    private WorkingSetGroup workingSetGroup;

    // constants
    private static final int SIZING_TEXT_FIELD_WIDTH = 250;

    /**
     * Creates a new project creation wizard page.
     *
     * @param pageName the name of this page
     */
    public WizardNewProjectWithAirSDKLocationPage(String pageName){
    	super(pageName);
        setPageComplete(false);
    }

    public String getSDKLocationName(){
    	return getSDKLocationValue();
    }


    /** (non-Javadoc)
     * Method declared on IDialogPage.
     */
    public void createControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NULL);
    

        initializeDialogUnits(parent);

        PlatformUI.getWorkbench().getHelpSystem().setHelp(composite,
                IIDEHelpContextIds.NEW_PROJECT_WIZARD_PAGE);

        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));

        createProjectNameGroup(composite);
        locationArea = new ProjectContentsLocationArea(getErrorReporter(), composite);
        if(initialProjectFieldValue != null) {
            locationArea.updateProjectName(initialProjectFieldValue);
        }
        
        // Scale the button based on the rest of the dialog
        setButtonLayoutData(locationArea.getBrowseButton());
        
        setPageComplete(validatePage());
        // Show description on opening
        setErrorMessage(null);
        setMessage(null);
        setControl(composite);
        Dialog.applyDialogFont(composite);
    }
    
    /**
     * Create a working set group for this page. This method can only be called
     * once.
     * 
     * @param composite
     *            the composite in which to create the group
     * @param selection
     *            the current workbench selection
     * @param supportedWorkingSetTypes
     *            an array of working set type IDs that will restrict what types
     *            of working sets can be chosen in this group
     * @return the created group. If this method has been called previously the
     *         original group will be returned.
     * @since 3.4
     */
    public WorkingSetGroup createWorkingSetGroup(Composite composite,
            IStructuredSelection selection, String[] supportedWorkingSetTypes) {
        if (workingSetGroup != null)
            return workingSetGroup;
        workingSetGroup = new WorkingSetGroup(composite, selection,
                supportedWorkingSetTypes);
        return workingSetGroup;
    }
    
    
    /**
     * Get an error reporter for the receiver.
     * @return IErrorMessageReporter
     */
    private IErrorMessageReporter getErrorReporter() {
        return new IErrorMessageReporter(){
            /* (non-Javadoc)
             * @see org.eclipse.ui.internal.ide.dialogs.ProjectContentsLocationArea.IErrorMessageReporter#reportError(java.lang.String)
             */
            public void reportError(String errorMessage, boolean infoOnly) {
                if (infoOnly) {
                    setMessage(errorMessage, IStatus.INFO);
                    setErrorMessage(null);
                }
                else
                    setErrorMessage(errorMessage);
                boolean valid = errorMessage == null;
                if(valid) {
                    valid = validatePage();
                }
                
                setPageComplete(valid);
            }
        };
    }

    /**
     * Creates the project name specification controls.
     *
     * @param parent the parent composite
     */
    private final void createProjectNameGroup(Composite parent) {
        // project specification group
        Composite projectGroup = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        projectGroup.setLayout(layout);
        projectGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        // new project label
        Label projectLabel = new Label(projectGroup, SWT.NONE);
        projectLabel.setText(IDEWorkbenchMessages.WizardNewProjectCreationPage_nameLabel);
        projectLabel.setFont(parent.getFont());

        // new project name entry field
        projectNameField = new Text(projectGroup, SWT.BORDER);
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        data.widthHint = SIZING_TEXT_FIELD_WIDTH;
        projectNameField.setLayoutData(data);
        projectNameField.setFont(parent.getFont());
        
        
        
        
        //package lablel
        Label packageNameLabel = new Label(projectGroup, SWT.NONE);
        packageNameLabel.setText("Main package name");
        packageNameLabel.setFont(parent.getFont());
        
        //package name input field
        packageNameField = new Text(projectGroup, SWT.BORDER);
        packageNameField.setFont(parent.getFont());
        data = new GridData(GridData.FILL_HORIZONTAL);
        data.widthHint = SIZING_TEXT_FIELD_WIDTH;
        packageNameField.setLayoutData(data);
        
        if(initialPackageFieldValue!=null){
        	packageNameField.setText(initialPackageFieldValue);
        }
        packageNameField.addListener(SWT.Modify, packageModifyListener);
        
        
        //sdk location 
        Label sdkLocationLabel = new Label(projectGroup, SWT.NONE);
        sdkLocationLabel.setText("AIR SDK Location");
        sdkLocationLabel.setFont(parent.getFont());
        
        //package name input field
        sdkLocationField = new Text(projectGroup, SWT.BORDER);
        sdkLocationField.setFont(parent.getFont());
        data = new GridData(GridData.FILL_HORIZONTAL);
        data.widthHint = SIZING_TEXT_FIELD_WIDTH;
        sdkLocationField.setLayoutData(data);
        sdkLocationField.setText(getDefaultSDKPath());
        
        sdkLocationField.addListener(SWT.Modify, sdkLocatioModifyListener);

        // Set the initial value first before listener
        // to avoid handling an event during the creation.
        if (initialProjectFieldValue != null) {
            projectNameField.setText(initialProjectFieldValue);
        }
        projectNameField.addListener(SWT.Modify, nameModifyListener);
    }

    public final static String aneAIRSDKPathKey = "ANE-Wizard-MainProject.airSdkPath";
    public static String getDefaultSDKPath(){
    	//if has remembered get it else try to find
    	String path = System.getProperty(aneAIRSDKPathKey);
    	if(path==null || path.isEmpty()){
    		/*String programName = System.getProperty("program.name");
    		if(programName==null || programName.isEmpty() || !programName.toLowerCase().contains("flash")){*/
    			File dir;
    	    	if(isMac()){
    	    		dir  = new File("/Applications");
    	    		return searchForSDKinDirectory(dir);
    	        }else{
    	        	dir = new File("C:/Program Files");
    	        	if(dir.exists()){
    	        		return searchForSDKinDirectory(dir);
    	        	}
    	        }
    		/*}else{
    			
    		}*/
	    	
    	}
    	return path;
    }
    public static boolean isValidSDKPath(String path){
    	if(path==null || path.isEmpty()){
    		return false;
    	}
    	File dir = new File(path+"/lib/adt.jar");
    	if(dir!=null && dir.exists()){
    		return true;
    	}
    	return false;
    }
    private static String searchForSDKinDirectory(File dir){
    	if (dir!=null && dir.exists())
		{
			File listDir[] = dir.listFiles();
            for (int i = 0; i < listDir.length; i++) {
            	File dir2 = listDir[i];
                if (dir2.isDirectory()) {
                      if(dir2.getName().toLowerCase().contains("flash builder")){
                    	  File listDir2[] = dir2.listFiles();
                    	  for (int j = 0; j < listDir2.length; j++) {
                    		  File dir1 = listDir2[j];
                    		  if (dir1.isDirectory()) {
                    			  if(dir1.getName().toLowerCase().contains("sdks")){
                    				  File listDir3[] = dir1.listFiles();
                    				  for (int k = 0; k < listDir3.length; k++) {
                    					 File dir3 = listDir3[k];
                    					 if (dir3.isDirectory()) {
                    						 String path = dir3.getAbsolutePath();
                    						 if(isValidSDKPath(path)){
                    							 //System.setProperty(aneAIRSDKPathKey, path);
                    							 try{
                    						        System.setProperty(WizardNewProjectWithAirSDKLocationPage.aneAIRSDKPathKey, path);
                    						     }catch(Exception e){}
                    							 return path;
                    						 }
									    }
									}
      	                    	}
      	                    }
                    	  }
                      }
                }
            }
		}
    	return "";
    }
    /**
     * Returns the current project location path as entered by 
     * the user, or its anticipated initial value.
     * Note that if the default has been returned the path
     * in a project description used to create a project
     * should not be set.
     *
     * @return the project location path or its anticipated initial value.
     */
    public IPath getLocationPath() {
        return new Path(locationArea.getProjectLocation());
    }
    
    /**
    /**
     * Returns the current project location URI as entered by 
     * the user, or <code>null</code> if a valid project location
     * has not been entered.
     *
     * @return the project location URI, or <code>null</code>
     * @since 3.2
     */
    public URI getLocationURI() {
        return locationArea.getProjectLocationURI();
    }

    /**
     * Creates a project resource handle for the current project name field
     * value. The project handle is created relative to the workspace root.
     * <p>
     * This method does not create the project resource; this is the
     * responsibility of <code>IProject::create</code> invoked by the new
     * project resource wizard.
     * </p>
     * 
     * @return the new project resource handle
     */
    public IProject getProjectHandle() {
        return ResourcesPlugin.getWorkspace().getRoot().getProject(
                getProjectName());
    }

    /**
     * Returns the current project name as entered by the user, or its anticipated
     * initial value.
     *
     * @return the project name, its anticipated initial value, or <code>null</code>
     *   if no project name is known
     */
    public String getProjectName() {
        if (projectNameField == null) {
            return initialProjectFieldValue;
        }

        return getProjectNameFieldValue();
    }
    
    /**
     * Returns the value of the project package name field
     * with leading and trailing spaces removed.
     * 
     * @return the project name in the field
     */
    private String getPackageNameFieldValue(){
    	if(packageNameField==null){
    		return "";
    	}
    	return packageNameField.getText().trim();
    }
    
    public void setInitialPackageName(String name) {
        if (name == null) {
            initialPackageFieldValue = null;
        } else {
        	initialPackageFieldValue = name.trim();
        }
        if(packageNameField!=null){
	        if(initialPackageFieldValue!=null){
	        	packageNameField.setText(initialPackageFieldValue);
	        }
	        else{
	        	packageNameField.setText("");
	        }
        }
    }
    /**
     * Returns the value of the project package name field
     * with leading and trailing spaces removed.
     * 
     * @return the project name in the field
     */
    public String getPackageName(){
    	return getPackageNameFieldValue();
    }
    
    private String getSDKLocationValue(){
    	if(sdkLocationField==null){
    		return "";
    	}
    	return sdkLocationField.getText().trim();
    }
    
    
    /**
     * Returns the value of the project name field
     * with leading and trailing spaces removed.
     * 
     * @return the project name in the field
     */
    private String getProjectNameFieldValue() {
        if (projectNameField == null) {
            return ""; //$NON-NLS-1$
        }

        return projectNameField.getText().trim();
    }
    
    /**
     * Sets the initial project name that this page will use when
     * created. The name is ignored if the createControl(Composite)
     * method has already been called. Leading and trailing spaces
     * in the name are ignored.
     * Providing the name of an existing project will not necessarily 
     * cause the wizard to warn the user.  Callers of this method 
     * should first check if the project name passed already exists 
     * in the workspace.
     * 
     * @param name initial project name for this page
     * 
     * @see IWorkspace#validateName(String, int)
     * 
     */
    public void setInitialProjectName(String name) {
        if (name == null) {
            initialProjectFieldValue = null;
        } else {
            initialProjectFieldValue = name.trim();
            if(locationArea != null) {
                locationArea.updateProjectName(name.trim());
            }
        }
    }

    /**
     * Set the location to the default location if we are set to useDefaults.
     */
    void setLocationForSelection() {
        locationArea.updateProjectName(getProjectNameFieldValue());
    }

  
    /**
     * Returns whether this page's controls currently all contain valid 
     * values.
     *
     * @return <code>true</code> if all controls are valid, and
     *   <code>false</code> if at least one is invalid
     */
    protected boolean validatePage() {
        IWorkspace workspace = IDEWorkbenchPlugin.getPluginWorkspace();

        
        String packageNameContents = getPackageNameFieldValue();
        if(packageNameContents==null || packageNameContents.equals("")){
        	setErrorMessage("Package name cannot be empty!");
        	setMessage("Package name cannot be empty!");
        	return false;
        }
        if(packageNameContents.endsWith(".") || packageNameContents.startsWith(".")){
        	setErrorMessage("Package name cannot start or begin with a '.' character");
        	setMessage("Package name cannot start or begin with a '.' character!");
        	return false;
        }
        boolean lastWasDot = true;
        for (int i = 0; i < packageNameContents.length(); i++) {
			char m = packageNameContents.charAt(i);//i;
			
			if(!(Character.isLetter(m) || (!lastWasDot && Character.isDigit(m)) || m=='.' || m=='_')){
				setErrorMessage("Package name is not valid! Use only Letters, numbers '.' or '_'");
	        	setMessage("Package name is not valid!");
				return false;
			}else if (lastWasDot && Character.isDigit(m)){
				setErrorMessage("Package name cannot start with a number is not valid!");
	        	setMessage("Package name cannot start with a number is not validd!");
				return false;
			}
			if(m=='.'){
				lastWasDot = true;
			}else{
				lastWasDot = false;
			}
		}
        
        
        
        String sdkLocationContents = getSDKLocationValue();
        if (sdkLocationContents.equals("")) { //$NON-NLS-1$
            setErrorMessage(null);
            setMessage("Must setup SDK Location");
            return false;
        }
       
        if(!isValidSDKPath(sdkLocationContents)){
        	setErrorMessage("Path to sdk not valid!");
            setMessage("Path to sdk not valid!");
            return false;
        }
        String projectFieldContents = getProjectNameFieldValue();
        if (projectFieldContents.equals("")) { //$NON-NLS-1$
            setErrorMessage(null);
            setMessage(IDEWorkbenchMessages.WizardNewProjectCreationPage_projectNameEmpty);
            return false;
        }

        IStatus nameStatus = workspace.validateName(projectFieldContents,
                IResource.PROJECT);
        if (!nameStatus.isOK()) {
            setErrorMessage(nameStatus.getMessage());
            return false;
        }

        IProject handle = getProjectHandle();
        if (handle.exists()) {
            setErrorMessage(IDEWorkbenchMessages.WizardNewProjectCreationPage_projectExistsMessage);
            return false;
        }
                
        IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(
                getProjectNameFieldValue());
        locationArea.setExistingProject(project);
        
        String validLocationMessage = locationArea.checkValidLocation();
        if (validLocationMessage != null) { // there is no destination location given
            setErrorMessage(validLocationMessage);
            return false;
        }

        setErrorMessage(null);
        setMessage(null);
        return true;
    }

    /*
     * see @DialogPage.setVisible(boolean)
     */
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            projectNameField.setFocus();
        }
    }

    /**
     * Returns the useDefaults.
     * @return boolean
     */
    public boolean useDefaults() {
        return locationArea.isDefault();
    }

    /**
     * Return the selected working sets, if any. If this page is not configured
     * to interact with working sets this will be an empty array.
     * 
     * @return the selected working sets
     * @since 3.4
     */
    public IWorkingSet[] getSelectedWorkingSets() {
        return workingSetGroup == null ? new IWorkingSet[0] : workingSetGroup
                .getSelectedWorkingSets();
    }
}
