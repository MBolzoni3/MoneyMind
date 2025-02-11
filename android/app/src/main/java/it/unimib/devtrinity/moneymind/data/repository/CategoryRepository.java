package it.unimib.devtrinity.moneymind.data.repository;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

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

    public CategoryRepository(Application application) {
        super(application, null, TAG);
        this.categoryDao = DatabaseClient.getInstance(application).categoryDao();
    }

    public LiveData<List<CategoryEntity>> getAllCategories() {
        return categoryDao.selectAll();
    }

    @Override
    protected CompletableFuture<Long> syncLocalToRemoteAsync() {
        return CompletableFuture.supplyAsync(() -> categoryDao.getLastSyncedTimestamp(), executorService);
    }

    @Override
    protected CompletableFuture<Void> syncRemoteToLocalAsync(long lastSyncedTimestamp) {
        return runFirestoreCategoryQuery(lastSyncedTimestamp)
                .thenAcceptAsync(querySnapshot -> {
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
                }, executorService)
                .exceptionally(e -> {
                    Log.e(TAG, "Error downloading categories", e);
                    return null;
                });
    }

    private CompletableFuture<QuerySnapshot> runFirestoreCategoryQuery(long lastSyncedTimestamp) {
        CompletableFuture<QuerySnapshot> future = new CompletableFuture<>();

        FirestoreHelper.getInstance().getGlobalCollection(COLLECTION_NAME)
                .whereGreaterThan("updatedAt", new Timestamp(lastSyncedTimestamp, 0))
                .orderBy("order", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(executorService, future::complete)
                .addOnFailureListener(e -> future.completeExceptionally(new RuntimeException("Error fetching categories", e)));

        return future;
    }

}
