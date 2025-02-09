package it.unimib.devtrinity.moneymind.data.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import it.unimib.devtrinity.moneymind.data.local.DatabaseClient;
import it.unimib.devtrinity.moneymind.data.local.dao.CategoryDao;
import it.unimib.devtrinity.moneymind.data.local.entity.CategoryEntity;
import it.unimib.devtrinity.moneymind.utils.google.FirestoreHelper;

public class CategoryRepository extends GenericRepository {

    private static final String TAG = CategoryRepository.class.getSimpleName();
    private static final String COLLECTION_NAME = "categories";

    private final CategoryDao categoryDao;

    public CategoryRepository(Context context) {
        super(context, null, TAG);
        this.categoryDao = DatabaseClient.getInstance(context).categoryDao();
    }

    public LiveData<List<CategoryEntity>> getAllCategories() {
        return categoryDao.selectAll();
    }

    @Override
    public void sync() {
        Timestamp timestamp = categoryDao.getLastSyncedTimestamp();
        sync(timestamp == null ? new Timestamp(0, 0).getSeconds() : timestamp.getSeconds());
    }

    @Override
    protected CompletableFuture<Void> syncRemoteToLocalAsync(long lastSyncedTimestamp) {
        return CompletableFuture.runAsync(() -> {
            FirestoreHelper.getInstance().getGlobalCollection(COLLECTION_NAME)
                    .whereGreaterThan("updatedAt", new Timestamp(lastSyncedTimestamp, 0))
                    .orderBy("order", Query.Direction.ASCENDING)
                    .get()
                    .addOnSuccessListener(executorService, querySnapshot -> {
                        List<CategoryEntity> categories = new ArrayList<>();
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            CategoryEntity category = document.toObject(CategoryEntity.class);
                            if (category != null) {
                                category.setFirestoreId(document.getId());
                                categories.add(category);
                            }
                        }

                        categoryDao.insert(categories);
                        Log.d(TAG, "Categories downloaded/updated (" + categories.size() + ")");
                    })
                    .addOnFailureListener(e -> {
                        throw new RuntimeException("Error downloading categories: " + e.getMessage(), e);
                    });
        }, executorService);
    }

}
