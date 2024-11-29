package it.unimib.devtrinity.moneymind.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import it.unimib.devtrinity.moneymind.data.local.dao.BudgetDao;
import it.unimib.devtrinity.moneymind.data.local.dao.CategoryDao;
import it.unimib.devtrinity.moneymind.data.local.dao.GoalDao;
import it.unimib.devtrinity.moneymind.data.local.dao.TransactionDao;
import it.unimib.devtrinity.moneymind.data.local.entity.BudgetEntity;
import it.unimib.devtrinity.moneymind.data.local.entity.CategoryEntity;
import it.unimib.devtrinity.moneymind.data.local.entity.GoalEntity;
import it.unimib.devtrinity.moneymind.data.local.entity.TransactionEntity;
import it.unimib.devtrinity.moneymind.utils.TimestampConverter;

@Database(entities = {
        BudgetEntity.class,
        CategoryEntity.class,
        GoalEntity.class,
        TransactionEntity.class,
        //UserEntity.class
}, version = 1, exportSchema = false)
@TypeConverters({TimestampConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract BudgetDao budgetDao();

    public abstract CategoryDao categoryDao();

    public abstract GoalDao goalDao();

    public abstract TransactionDao transactionDao();
    //public abstract UserDao userDao();

}

