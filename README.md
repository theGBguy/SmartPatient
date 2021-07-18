# SmartPatient
It is an android application primarily focused to remind the patient for their medicine intake.
This repository contains two versions of the implementation; offline version(in master branch) and full-fletched version(in dev-ongoing branch).

**Screenshots:**

<img src="https://user-images.githubusercontent.com/25641763/126058268-044485d4-63e9-4bb3-849f-e02bf596141a.jpg" width="18%"></img>
<img src="https://user-images.githubusercontent.com/25641763/126058280-61004309-34e4-4797-936c-fa9a7b29fd2f.jpg" width="18%"></img> 
<img src="https://user-images.githubusercontent.com/25641763/126058267-228b30ad-8369-4b4f-ae73-1000998c87fd.jpg" width="18%"></img>
<img src="https://user-images.githubusercontent.com/25641763/126058276-7812ee80-ae94-4a0c-9a9f-467415c636cc.jpg" width="18%"></img>
<img src="https://user-images.githubusercontent.com/25641763/126058277-91e72dc4-b567-4bc9-9661-3183ae698c62.jpg" width="18%"></img>
<img src="https://user-images.githubusercontent.com/25641763/126058270-4b7786c8-22f2-4c34-8e24-738b75de6693.jpg" width="18%"></img>
<img src="https://user-images.githubusercontent.com/25641763/126058275-4a14c3b8-0790-4455-905f-cef5a492864c.jpg" width="18%"></img>
<img src="https://user-images.githubusercontent.com/25641763/126058272-28a84e00-11a3-47b9-9faa-4d088bb3366e.jpg" width="18%"></img>
<img src="https://user-images.githubusercontent.com/25641763/126058279-a57d5afe-eb35-4aa3-844c-fc2d3b9c0956.jpg" width="18%"></img>
<img src="https://user-images.githubusercontent.com/25641763/126058265-9b0435ff-1ca4-47f4-ab83-1de22947a9b9.jpg" width="18%"></img>
<img src="https://user-images.githubusercontent.com/25641763/126058266-060af9a2-ac7f-478a-9884-fe73f7bc15ee.jpg" width="18%"></img>
<img src="https://user-images.githubusercontent.com/25641763/126058269-d70edc57-e949-4b5f-a53a-4bb3c05325ee.jpg" width="18%"></img>
<img src="https://user-images.githubusercontent.com/25641763/126058281-d00ad4a5-0764-4471-9bca-edfd95d1fa65.jpg" width="18%"></img> 

**Features of offline version:**
1. It can schedule alarms for medicine information entered via AddNewMedicine screen. 
2. Details of medicine along with it's image and useful information(if was entered while scheduling alarms) would be displayed during the time of alarm.
3. Alarms that are unresponded are flagged as missed meanwhile responded alarms are flagged as completed. All of pending, completed and missed alarms can be seen in their individual tabs.

**Features of online version:**
1. Patients and Health Personnel can create their account and logs in.
2. Patient can request appointment with the health personnel of his/her choice from the available health personnel list.
3. Health Personnel can respond to such request by either accepting or rejecting the request. In case of acceptance, it is assumed that both health personnel and patient will meet at the selected time which would be specified during the appointment creation.
4. The medicine prescribed by health personnel can be entered along with the appropriate time for it's intake by health personnel. Such information would be propagated to the patient's app and alarms are scheduled accordingly.
5. In case of queries, patient can connect with health personnel with the help of simple chat features implemented in the app.

**Key things that I learned during the development of this app:**
1. how to implement MVVM properly,
2. how to handle authentication using Firebase Authentication and about login flow,
3. how to use Firestore and Firebase Storage,
4. how to prevent memory leaks in android, and
5. how to unit test the app.

**Libraries used:**
1. Hilt(for dependency injection)
2. Navigation Components(for navigation)
3. Firebase(for authentication and data storage)
4. Room(for local persistence)
5. Livedata and lifecycle(for reactive and better programming)
6. Timber(for logging)
