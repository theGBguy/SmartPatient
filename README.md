# SmartPatient
It is an android application primarily focused to remind the patient for their medicine intake.
This repository contains two versions of the implementation; offline version(in master branch) and full-fletched version(in dev-ongoing branch).

Features of offline version:
1. It can schedule alarms for medicine information entered via AddNewMedicine screen. 
2. Details of medicine along with it's image and useful information(if was entered while scheduling alarms) would be displayed during the time of alarm.
3. Alarms that are unresponded are flagged as missed meanwhile responded alarms are flagged as completed. All of pending, completed and missed alarms can be seen in their individual tabs.

Features of online version:
1. Patients and Health Personnel can create their account and logs in.
2. Patient can request appointment with the health personnel of his/her choice from the available health personnel list.
3. Health Personnel can respond to such request by either accepting or rejecting the request. In case of acceptance, it is assumed that both health personnel and patient will meet at the selected time which would be specified during the appointment creation.
4. The medicine prescribed by health personnel can be entered along with the appropriate time for it's intake by health personnel. Such information would be propagated to the patient's app and alarms are scheduled accordingly.
5. In case of queries, patient can connect with health personnel with the help of simple chat features implemented in the app.

Key things that I learned during the development of this app:
1. how to implement MVVM properly,
2. how to handle authentication using Firebase Authentication and about login flow,
3. how to use Firestore and Firebase Storage,
4. how to prevent memory leaks in android, and
5. how to unit test the app.

Libraries used:
1. Hilt(for dependency injection)
2. Navigation Components(for navigation)
3. Firebase(for authentication and data storage)
4. Room(for local persistence)
5. Livedata and lifecycle(for reactive and better programming)
6. Timber(for logging)
