package it.unimib.devtrinity.moneymind.data.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.unimib.devtrinity.moneymind.data.local.DatabaseClient;
import it.unimib.devtrinity.moneymind.data.local.dao.CategoryDao;
import it.unimib.devtrinity.moneymind.data.local.entity.CategoryEntity;
import it.unimib.devtrinity.moneymind.utils.GenericCallback;
import it.unimib.devtrinity.moneymind.utils.google.FirestoreHelper;

public class CategoryRepository extends GenericRepository {
    private static final String COLLECTION_NAME = "categories";

    private final CategoryDao categoryDao;

    public CategoryRepository(Context context) {
        super();
        this.categoryDao = DatabaseClient.getInstance(context).categoryDao();
    }

    public LiveData<List<CategoryEntity>> getAllCategories() {
        return categoryDao.selectAll();
    }

    public void getLastSyncedTimestamp(GenericCallback<Timestamp> callback) {
        executorService.execute(() -> {
            try {
                Timestamp timestamp = categoryDao.getLastSyncedTimestamp();
                callback.onSuccess(timestamp);
            } catch (Exception e) {
                callback.onFailure(e.getMessage());
                Log.e(this.getClass().getSimpleName(), e.getMessage(), e);
            }
        });
    }

    private void insertCategories(List<CategoryEntity> categories) {
        executorService.execute(() -> categoryDao.insert(categories));
    }

    public void syncCategories(Timestamp lastSyncedTimestamp) {
        if (lastSyncedTimestamp == null) {
            lastSyncedTimestamp = new Timestamp(new Date(0));
        }

        Query query = FirestoreHelper.getInstance().getGlobalCollection(COLLECTION_NAME)
                .whereGreaterThan("lastUpdated", lastSyncedTimestamp);

        FirestoreHelper.getInstance().getDocuments(query, new GenericCallback<>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                try {
                    List<CategoryEntity> categories = new ArrayList<>();
                    querySnapshot.forEach(document -> {
                        categories.add(new CategoryEntity(
                                document.getId(),
                                document.getString("name"),
                                Boolean.TRUE.equals(document.getBoolean("deleted")),
                                document.getTimestamp("createdAt"),
                                document.getTimestamp("updatedAt")
                        ));
                    });

                    insertCategories(categories);

                    Log.d(this.getClass().getSimpleName(), "Categories downloaded/updated (" + categories.size() + ")");
                } catch (Exception e) {
                    Log.e(this.getClass().getSimpleName(), e.getMessage(), e);
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e(this.getClass().getSimpleName(), "Error downloading categories.\n" + errorMessage);
            }
        });
    }

}
