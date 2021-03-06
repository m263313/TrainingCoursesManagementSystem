package ua.ukma.nc.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ua.ukma.nc.dao.GroupAttachmentDao;
import ua.ukma.nc.entity.GroupAttachment;
import ua.ukma.nc.entity.impl.proxy.GroupProxy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * Created by Алексей on 30.10.2016.
 */
@Repository
public class GroupAttachmentDaoImpl implements GroupAttachmentDao{

    private static Logger log = LoggerFactory.getLogger(GroupAttachmentDaoImpl.class.getName());

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ApplicationContext context;

    public class GroupAttachmentMapper implements RowMapper<GroupAttachment> {
        public GroupAttachment mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            GroupAttachment groupAttachment = new GroupAttachment();
            groupAttachment.setId(resultSet.getLong("id"));
            groupAttachment.setName(resultSet.getString("name"));
            groupAttachment.setAttachment(resultSet.getBytes("attachment"));
            groupAttachment.setGroup(context.getBean(GroupProxy.class,resultSet.getLong("id_group")));
            groupAttachment.setAttachmentScope(resultSet.getString("attachment_scope"));
            return groupAttachment;
        }
    }

    private static final String GET_ALL = "SELECT id, name, id_group, attachment_scope, attachment FROM tcms.group_attachment";

    private static final String GET_BY_ID = "SELECT id, name, id_group, attachment_scope, attachment FROM tcms.group_attachment WHERE id = ?";

    private static final String DELETE_GROUP_ATTACHMENT = "DELETE FROM tcms.group_attachment WHERE id = ?";

    private static final String CREATE_GROUP_ATTACHMENT = "INSERT INTO tcms.group_attachment (name, id_group, attachment_scope, attachment) VALUES (?,?,?,?)";

    private static final String UPDATE_GROUP_ATTACHMENT = "UPDATE tcms.group_attachment SET name = ?, id_group = ?, attachment_scope = ?, attachment = ? WHERE id = ?";
    
    private static final String GET_BY_GROUP_ID = "SELECT * FROM tcms.group_attachment WHERE id_group = ?"; 
    @Override
    public GroupAttachment getById(Long id) {
        log.info("Getting group attachment with id = {}", id);
        return jdbcTemplate.queryForObject(GET_BY_ID, new GroupAttachmentMapper(), id);
    }

    @Override
    public int deleteGroupAttachment(GroupAttachment groupAttachment) {
        log.info("Deleting group attachment with id = {}", groupAttachment.getId());
        return jdbcTemplate.update(DELETE_GROUP_ATTACHMENT, groupAttachment.getId());
    }

    @Override
    public int updateGroupAttachment(GroupAttachment groupAttachment) {
        log.info("Updating group attachment with id = {}", groupAttachment.getId());
        return jdbcTemplate.update(UPDATE_GROUP_ATTACHMENT, groupAttachment.getName(), groupAttachment.getGroup().getId(), groupAttachment.getAttachmentScope(), groupAttachment.getAttachment(), groupAttachment.getId());
    }

    @Override
    public List<GroupAttachment> getAll() {
        log.info("Getting all group attachments");
        return jdbcTemplate.query(GET_ALL, new GroupAttachmentMapper());
    }

    @Override
    public Long createGroupAttachment(GroupAttachment groupAttachment) {
    	KeyHolder holder = new GeneratedKeyHolder();

    	jdbcTemplate.update(new PreparedStatementCreator() {           

    	                @Override
    	                public PreparedStatement createPreparedStatement(Connection connection)
    	                        throws SQLException {
    	                    PreparedStatement ps = connection.prepareStatement(CREATE_GROUP_ATTACHMENT, Statement.RETURN_GENERATED_KEYS);
    	                    ps.setString(1, groupAttachment.getName());
    	                    ps.setLong(2, groupAttachment.getGroup().getId());
    	                    ps.setString(3, groupAttachment.getAttachmentScope());
    	                    ps.setBytes(4, groupAttachment.getAttachment());
    	                    return ps;
    	                }
    	            }, holder);

    	return (Long) holder.getKeys().get("id");
    }

	@Override
	public List<GroupAttachment> getByGroupId(Long groupId) {
		log.info("Getting all group attachments with group id = {}", groupId);
		return jdbcTemplate.query(GET_BY_GROUP_ID,new GroupAttachmentMapper(),groupId );
	}
}
