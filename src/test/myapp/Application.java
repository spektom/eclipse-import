package test.myapp;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

public class Application implements IApplication {

	public Object start(IApplicationContext context) throws Exception {

		String[] args = (String[]) context.getArguments().get(IApplicationContext.APPLICATION_ARGS);

		boolean build = false;

		// Determine projects to import
		List<String> projects = new LinkedList<String>();
		for (int i = 0; i < args.length; ++i) {
			if ("-import".equals(args[i]) && i + 1 < args.length) {
				projects.add(args[++i]);
			} else if ("-build".equals(args[i])) {
				build = true;
			}
		}

		if (projects.size() == 0) {
			System.out.println("No projects to import!");
		} else {
			for (String projectPath : projects) {
				System.out.println("Importing project from: " + projectPath);

				// Import project description:
				IProjectDescription description = ResourcesPlugin.getWorkspace().loadProjectDescription(
						new Path(projectPath).append(".project"));
				IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(description.getName());
				project.create(description, null);
				project.open(null);
			}

			// Build all projects after importing
			if (build) {
				System.out.println("Re-building workspace");
				ResourcesPlugin.getWorkspace().build(IncrementalProjectBuilder.CLEAN_BUILD, new NullProgressMonitor());
				ResourcesPlugin.getWorkspace().build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
			}
		}
		return null;
	}

	public void stop() {
	}
}
