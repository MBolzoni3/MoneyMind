package it.unimib.devtrinity.moneymind.utils.google;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirestoreHelper {

    private static final String TAG = FirestoreHelper.class.getSimpleName();

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

    public CollectionReference getUserCollection(String collectionName) {
        return firestore.collection("users").document(getUserId()).collection(collectionName);
    }

    public CollectionReference getGlobalCollection(String collectionName) {
        return firestore.collection(collectionName);
    }

}

