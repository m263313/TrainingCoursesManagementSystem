package ua.ukma.nc.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import ua.ukma.nc.dao.StatusLogDao;
import ua.ukma.nc.entity.StatusLog;
import ua.ukma.nc.service.StatusLogService;

import java.util.List;

/**
 * Created by Алексей on 30.10.2016.
 */
@Service
public class StatusLogServiceImpl implements StatusLogService{

    @Autowired
    private StatusLogDao statusLogDao;

    @Override
    public StatusLog getById(Long id) {
        return statusLogDao.getById(id);
    }

    @Override
    public int deleteStatusLog(StatusLog statusLog) {
        return statusLogDao.deleteStatusLog(statusLog);
    }

    @Override
    public int updateStatusLog(StatusLog statusLog) {
        return statusLogDao.updateStatusLog(statusLog);
    }

    @Override
    public List<StatusLog> getAll() {
        return statusLogDao.getAll();
    }

    @Override
    public int createStatusLog(StatusLog statusLog) {
        return statusLogDao.createStatusLog(statusLog);
    }

	@Override
	public List<StatusLog> getByProjectStudent(Long projectId, Long studentId) {
		return statusLogDao.getByProjectStudent(projectId, studentId);
	}

    @Override
    public int getNumOfStartedProject(Long projectId) {
        return statusLogDao.getNumOfStartedProject(projectId);
    }

    @Override
    public int getNumOfInvitedByProject(Long projectId) {
        return statusLogDao.getNumOfInvitedByProject(projectId);
    }

    @Override
    public int getNumOfJobOffersByProject(Long projectId) {
        return statusLogDao.getNumOfJobOffersByProject(projectId);
    }

    @Override
	public Long getNewestGroup(Long userId) {
		return statusLogDao.getNewestGroup(userId);
	}

	@Override
	public boolean exists(Long userId) {
		return statusLogDao.exists(userId);
	}

	@Override
	public StatusLog getLast(Long groupId, Long studentId) {
		return statusLogDao.getLast(groupId, studentId);
	}

	@Override
	public int removeStatusLogs(Long groupId, Long userId) {
		return statusLogDao.removeStatusLogs(groupId, userId);
	}
}
