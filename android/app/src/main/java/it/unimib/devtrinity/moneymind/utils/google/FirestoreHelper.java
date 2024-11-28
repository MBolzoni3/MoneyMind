package it.unimib.devtrinity.moneymind.utils.google;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

import it.unimib.devtrinity.moneymind.utils.GenericCallback;

public class FirestoreHelper {
    private static final String TAG = "FirestoreHelper";

    private static FirestoreHelper instance;

    public static FirestoreHelper getInstance() {
        if (instance == null) {
            instance = new FirestoreHelper();
        }
        return instance;
    }

    private final FirebaseFirestore firestore;

    private FirestoreHelper() {
        firestore = FirebaseFirestore.getInstance();
    }

    private String getUserId() {
        if (FirebaseHelper.getInstance().getCurrentUser() == null) {
            throw new IllegalStateException("User is not logged in");
        }
        return FirebaseHelper.getInstance().getCurrentUser().getUid();
    }

    public CollectionReference getCollection(String collectionName) {
        return firestore.collection("users").document(getUserId()).collection(collectionName);
    }

    public void addDocument(String collectionName, Map<String, Object> data, GenericCallback<String> callback) {
        getCollection(collectionName)
                .add(data)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Document added with ID: " + documentReference.getId());
                    callback.onSuccess(documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding document", e);
                    callback.onFailure(e.getMessage());
                });
    }

    public void getDocuments(String collectionName, Query query, GenericCallback<QuerySnapshot> callback) {
        query.get()
                .addOnSuccessListener(querySnapshot -> {
                    Log.d(TAG, "Documents fetched: " + querySnapshot.size());
                    callback.onSuccess(querySnapshot);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching documents", e);
                    callback.onFailure(e.getMessage());
                });
    }
}

