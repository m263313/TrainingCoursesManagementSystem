package ua.ukma.nc.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ua.ukma.nc.dao.ProjectDao;
import ua.ukma.nc.entity.Criterion;
import ua.ukma.nc.entity.Project;
import ua.ukma.nc.entity.Role;
import ua.ukma.nc.query.ProjectParamResolver;
import ua.ukma.nc.query.ProjectSearch;
import ua.ukma.nc.service.ProjectService;
import ua.ukma.nc.service.RoleService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Алексей on 30.10.2016.
 */
@Service
public class ProjectServiceImpl implements ProjectService {
	
	@Autowired
	private RoleService roleService;

	@Autowired
	private ProjectParamResolver projectParamResolver;

	@Autowired
	private ProjectDao projectDao;

	@Override
	public Project getById(Long id) {
		return projectDao.getById(id);
	}
	
	@Override
	public Project getByName(String name){
		return projectDao.getByName(name);
	}
	
	@Override
	public int deleteProject(Project project) {
		return projectDao.deleteProject(project);
	}

	@Override
	public int updateProject(Project project) {
		return projectDao.updateProject(project);
	}

	@Override
	public List<Project> getAll() {
		return projectDao.getAll();
	}

	@Override
	public int createProject(Project project) {
		return projectDao.createProject(project);
	}

	@Override
	public List<Project> search(ProjectSearch projectSearch) {
		return projectDao.query(projectParamResolver.getQueryBuilder(projectSearch).generateQuery());
	}

	@Override
	public Integer getMaxPage(ProjectSearch projectSearch) {
		int maxPage = projectDao.count(projectParamResolver.getQueryBuilder(projectSearch).generateCountQuery());

		if (maxPage % ProjectParamResolver.ITEMS_PER_PAGE == 0 && maxPage != 0)
			maxPage /= ProjectParamResolver.ITEMS_PER_PAGE;
		else
			maxPage = maxPage / ProjectParamResolver.ITEMS_PER_PAGE + 1;

		return maxPage;
	}

	@Override
	public List<Project> getStudentProjects(Long userId) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String name = authentication.getName();
		
		List<Role> roles = roleService.getCurrentUserRoles(name, userId);
		
		for(Role role: roles)
			if(role.getTitle().equals("ROLE_STUDENT"))
				return new ArrayList<Project>();
		
		return projectDao.getStudentProjects(userId);
	}
	
	@Override
	public List<Project> getMentorProjects(Long userId) {
		return projectDao.getMentorProjects(userId);
	}

	@Override
	public int addCriteria(Long projectId, Criterion criterion) {
		return projectDao.addCriteria(projectId, criterion);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public int deleteProjectCriterion(Long projectId, Criterion criterion) {
		projectDao.deleteCriterionInAllProjectMeetings(projectId, criterion);
		projectDao.deleteProjectCriterion(projectId, criterion);
		return 1;
	}

	@Override
	public int deleteCriterionInAllProjectMeetings(Long projectId, Criterion criterion) {
		return projectDao.deleteCriterionInAllProjectMeetings(projectId, criterion);
	}
}
