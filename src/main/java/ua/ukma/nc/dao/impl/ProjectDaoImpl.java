package ua.ukma.nc.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ua.ukma.nc.dao.ProjectDao;
import ua.ukma.nc.entity.Project;
import ua.ukma.nc.entity.impl.real.ProjectImpl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Алексей on 30.10.2016.
 */
@Repository
public class ProjectDaoImpl implements ProjectDao{

    private static Logger log = LoggerFactory.getLogger(ProjectDaoImpl.class.getName());

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public class ProjectMapper implements RowMapper<Project> {
        public Project mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            Project project = new ProjectImpl();
            project.setId(resultSet.getLong("id"));
            project.setName(resultSet.getString("name"));
            project.setDescription(resultSet.getString("description"));
            project.setStartDate(resultSet.getTimestamp("start"));
            project.setFinishDate(resultSet.getTimestamp("finish"));
            return project;
        }
    }

    private static final String GET_ALL = "SELECT id, name, description, start, finish FROM tcms.project";

    private static final String GET_BY_ID = "SELECT id, name, description, start, finish FROM tcms.project WHERE id = ?";

    private static final String DELETE_PROJECT = "DELETE FROM tcms.project WHERE id = ?";

    private static final String CREATE_PROJECT = "INSERT INTO tcms.project (name, description, start, finish) VALUES (?,?,?,?)";

    private static final String UPDATE_PROJECT = "UPDATE tcms.project SET name = ?, description = ?, start = ?, finish = ? WHERE id = ?";

    @Override
    public Project getById(Long id) {
        log.info("Getting project with id = {}", id);
        return jdbcTemplate.queryForObject(GET_BY_ID, new ProjectMapper(), id);
    }

    @Override
    public int deleteProject(Project project) {
        log.info("Deleting project with id = {}", project.getId());
        return jdbcTemplate.update(DELETE_PROJECT, project.getId());
    }

    @Override
    public int updateProject(Project project) {
        log.info("Updating project with id = {}", project.getId());
        return jdbcTemplate.update(UPDATE_PROJECT,project.getName(),project.getDescription(), project.getStartDate(), project.getFinishDate(), project.getId());
    }

    @Override
    public List<Project> getAll() {
        log.info("Getting all projects");
        return jdbcTemplate.query(GET_ALL, new ProjectMapper());
    }

    @Override
    public int createProject(Project project) {
        log.info("Create new project with name = {}", project.getName());
        return jdbcTemplate.update(CREATE_PROJECT, project.getName(), project.getDescription(), project.getStartDate(), project.getFinishDate());
    }
}
