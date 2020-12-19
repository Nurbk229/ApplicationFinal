# Shopping App Using Firebase (Admin & User)

```groovy
dependencies {
    // FirebaseUI for Firebase Realtime Database
    implementation 'com.firebaseui:firebase-ui-database:7.1.1'

    // FirebaseUI for Cloud Firestore
    implementation 'com.firebaseui:firebase-ui-firestore:7.1.1'

    // FirebaseUI for Firebase Auth
    implementation 'com.firebaseui:firebase-ui-auth:7.1.1'

    // FirebaseUI for Cloud Storage
    implementation 'com.firebaseui:firebase-ui-storage:7.1.1'
}
```

## Usage

FirebaseUI has separate modules for using Firebase Realtime Database, Cloud Firestore,
Firebase Auth, and Cloud Storage. To get started, see the individual instructions for each module:

* [FirebaseUI Auth](auth/README.md)
* [FirebaseUI Firestore](firestore/README.md)
* [FirebaseUI Database](database/README.md)
* [FirebaseUI Storage](storage/README.md)
### Compatibility with Firebase / Google Play Services libraries

FirebaseUI libraries have the following transitive dependencies on the Firebase SDK:
```
firebase-ui-auth
|--- com.google.firebase:firebase-auth
|--- com.google.android.gms:play-services-auth

firebase-ui-database
|--- com.google.firebase:firebase-database

firebase-ui-firestore
|--- com.google.firebase:firebase-firestore

firebase-ui-storage
|--- com.google.firebase:firebase-storage
```

#### Realtime Database

```groovy
implementation "com.google.firebase:firebase-database:$X.Y.Z"

implementation "androidx.legacy:legacy-support-v4:$X.Y.Z"
implementation "androidx.recyclerview:recyclerview:$X.Y.Z"
```

#### Storage

```groovy
implementation "com.google.firebase:firebase-storage:$X.Y.Z"

implementation "androidx.legacy:legacy-support-v4:$X.Y.Z"

```


