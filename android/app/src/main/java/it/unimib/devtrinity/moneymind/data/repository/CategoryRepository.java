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
import java.util.Objects;

import it.unimib.devtrinity.moneymind.data.local.DatabaseClient;
import it.unimib.devtrinity.moneymind.data.local.dao.CategoryDao;
import it.unimib.devtrinity.moneymind.data.local.entity.CategoryEntity;
import it.unimib.devtrinity.moneymind.utils.GenericCallback;
import it.unimib.devtrinity.moneymind.utils.google.FirestoreHelper;

public class CategoryRepository extends GenericRepository {
    private static final String COLLECTION_NAME = "categories";

    private final CategoryDao categoryDao;

    public CategoryRepository(Context context) {
        this.categoryDao = DatabaseClient.getInstance(context).categoryDao();
    }

    public LiveData<List<CategoryEntity>> getAllCategories() {
        return categoryDao.selectAll();
    }

    public void syncCategories() {
        Timestamp timestamp = categoryDao.getLastSyncedTimestamp();

        syncLocalToRemote(timestamp);
    }

    public void syncLocalToRemote(Timestamp lastSyncedTimestamp) {
        if (lastSyncedTimestamp == null) {
            lastSyncedTimestamp = new Timestamp(new Date(0));
        }

        Query query = FirestoreHelper.getInstance().getGlobalCollection(COLLECTION_NAME)
                .whereGreaterThan("updatedAt", lastSyncedTimestamp)
                .orderBy("order", Query.Direction.ASCENDING);

        FirestoreHelper.getInstance().getDocuments(query, new GenericCallback<>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                try {
                    List<CategoryEntity> categories = new ArrayList<>();
                    querySnapshot.forEach(document -> {
                        categories.add(new CategoryEntity(
                                document.getId(),
                                document.getString("name"),
                                Objects.requireNonNull(document.getLong("order")).intValue(),
                                Boolean.TRUE.equals(document.getBoolean("deleted")),
                                document.getTimestamp("createdAt"),
                                document.getTimestamp("updatedAt")
                        ));
                    });

                    executorService.execute(() -> categoryDao.insert(categories));

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
