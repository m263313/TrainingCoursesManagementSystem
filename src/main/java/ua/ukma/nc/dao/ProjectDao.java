package ua.ukma.nc.dao;

import ua.ukma.nc.entity.Project;

import java.util.List;

/**
 * Created by Алексей on 30.10.2016.
 */
public interface ProjectDao {
	Project getById(Long id);
	
	Project getByName(String name);

	int deleteProject(Project project);

	int updateProject(Project project);

	List<Project> getAll();

	List<Project> query(String query);
	
	List<Project> getStudentProjects(Long userId);

	Integer count(String query);

	int createProject(Project project);
}
