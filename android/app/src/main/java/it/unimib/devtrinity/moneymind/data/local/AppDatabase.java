package it.unimib.devtrinity.moneymind.data.local;

import static it.unimib.devtrinity.moneymind.constant.Constants.DATABASE_VERSION;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import it.unimib.devtrinity.moneymind.data.local.converter.BigDecimalConverter;
import it.unimib.devtrinity.moneymind.data.local.converter.MovementTypeConverter;
import it.unimib.devtrinity.moneymind.data.local.converter.RecurrenceTypeConverter;
import it.unimib.devtrinity.moneymind.data.local.converter.TimestampConverter;
import it.unimib.devtrinity.moneymind.data.local.dao.BudgetDao;
import it.unimib.devtrinity.moneymind.data.local.dao.CategoryDao;
import it.unimib.devtrinity.moneymind.data.local.dao.ExchangeDao;
import it.unimib.devtrinity.moneymind.data.local.dao.GoalDao;
import it.unimib.devtrinity.moneymind.data.local.dao.RecurringTransactionDao;
import it.unimib.devtrinity.moneymind.data.local.dao.TransactionDao;
import it.unimib.devtrinity.moneymind.data.local.entity.BudgetEntity;
import it.unimib.devtrinity.moneymind.data.local.entity.CategoryEntity;
import it.unimib.devtrinity.moneymind.data.local.entity.ExchangeEntity;
import it.unimib.devtrinity.moneymind.data.local.entity.GoalEntity;
import it.unimib.devtrinity.moneymind.data.local.entity.RecurringTransactionEntity;
import it.unimib.devtrinity.moneymind.data.local.entity.TransactionEntity;

@Database(entities = {
        BudgetEntity.class,
        CategoryEntity.class,
        GoalEntity.class,
        TransactionEntity.class,
        RecurringTransactionEntity.class,
        ExchangeEntity.class
}, version = DATABASE_VERSION, exportSchema = false)
@TypeConverters({
        TimestampConverter.class,
        MovementTypeConverter.class,
        RecurrenceTypeConverter.class,
        BigDecimalConverter.class
})
public abstract class AppDatabase extends RoomDatabase {

    public abstract BudgetDao budgetDao();

    public abstract CategoryDao categoryDao();

    public abstract GoalDao goalDao();

    public abstract TransactionDao transactionDao();

    public abstract RecurringTransactionDao recurringTransactionDao();

    public abstract ExchangeDao exchangeDao();

}

