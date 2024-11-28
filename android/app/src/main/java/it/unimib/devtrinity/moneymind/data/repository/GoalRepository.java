package it.unimib.devtrinity.moneymind.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;

import it.unimib.devtrinity.moneymind.data.local.DatabaseClient;
import it.unimib.devtrinity.moneymind.data.local.dao.GoalDao;
import it.unimib.devtrinity.moneymind.data.local.entity.GoalEntity;

public class GoalRepository extends GenericRepository {

    private final GoalDao goalDao;

    public GoalRepository(Context context) {
        this.goalDao = DatabaseClient.getInstance(context).goalDao();
    }

    public LiveData<List<GoalEntity>> getAllGoals() {
        return goalDao.selectAll();
    }

    public void insertGoal(GoalEntity goal) {
        executorService.execute(() -> {
            goalDao.insert(goal);
        });
    }

    public void updateGoal(GoalEntity goal) {
        executorService.execute(() -> {
            goalDao.update(goal);
        });
    }

    public void deleteGoal(int goalId) {
        executorService.execute(() -> {
            goalDao.delete(goalId);
        });
    }

}
