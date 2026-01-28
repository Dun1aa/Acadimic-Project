#include <iostream>
#include <string>
#include <windows.h>  // مكتبه عشان نقدر ننستعمل الالوان 
using namespace std;

void setColor(int color) {  // فنكشن اللون
    SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE), color);
    /*
     setColor(10);للون الاخضر
     setColor(12);للون الاحمر 
     setColor(7); للون الابيض
     setColor(14);اللون الاصفر
     setColor(11); اللون الازرق
    */
}

class ToDoList {
private:
    struct node {
        node* next;
        int priority;
        string task;
        bool iscompleted;
        bool previousState;//عشان اذا بغى يتراجع عن تحديث مهمه
    }*head;

public:
    ToDoList();
    void add_task(int pro, string tk);
    void remove_task(int taskNumber);
    void update_task(int taskNumber);
    void search_task(int taskNumber);
    void display_task();
    int count_tasks();
};
ToDoList::ToDoList() {
    head = NULL;
}

//-------------------------------------------------(الاضافه ( دنا
// pro رقم الاولويه الي ادخله المستخدم
// tk المهمه الي اضافه المستخدم
void ToDoList::add_task(int pro,string tk){
// الحاله1: تكون لها اعلى اولويه او فارغه
if (head==NULL||pro<head->priority){
node*t=new node;
 t->next=head;
 t->priority=pro;
 t->task=tk;
 t->iscompleted = false;
 head=t;
}else{
// الحاله2: ادور مكانه الصحيح حسب اولويته
node*t=head;
while (t->next!=NULL&&t->next->priority<=pro){
// بينما المؤشر التالي اولويته اعلى من التاسك الجديده
      // اخلي المؤشر ينتقل له
      t=t->next;
  
}
// في حاله ان المؤشر التالي اولويته اقل راح اضع التاسك الجديده هنا
   node*newtask=new node;
      newtask->next=t->next;
      newtask->priority=pro;
      newtask->task=tk;
      t->next = newtask;//
      newtask->iscompleted = false;// 

} 
 setColor(10);
    cout << "\nTask added successfully:\n";
    cout << "Task: " << tk << " [Priority " << pro << "]"<<"->";
    setColor(7);

    setColor(12);
    cout<<"(Not Completed)\n";
    setColor(7);
   
}


//-------------------------------------------------(العرض (حور
void ToDoList::display_task() {
    cout << "\n===================================\n";
    cout << "           Task List\n";
    cout << "===================================\n";
    node* current = head; // سويت بوينتر  ياشر على اول وضيفه
    
    // الحاله الاولى اذ كانت فارغه
    if (current == NULL) {
        setColor(12);
        cout << "empty list.\n";
        setColor(7);
        return;
    }
     // الحاله الثانيه اذ لم تكن فارغه
    int taskNumber = 1;
    while (current != NULL) {  //يطبع التاسك مع الاولويه
        cout << taskNumber << ". " << current->task << " [Priority " << current->priority << "]";
        if (current->iscompleted) {// نوره
            setColor(10);
            cout << " (Completed)";
            setColor(7); 
        } else {
            setColor(12); 
            cout << " (Not Completed)";
            setColor(7); 
        }
        cout << endl;
        current = current->next; // رح للمهمه اللي بعده
        taskNumber++; 
    }
    cout << "===================================\n";
}

//-------------------------------------------------(التحديث ( نوره
void ToDoList::update_task(int taskNumber) {
    if (head==NULL) {// الحاله 1 : ان مافيه ولا مهمه بالقائمه
        setColor(12);
        cout << "No tasks available to update.\n";
        setColor(7);
        return; // return عشان مايكمل باقي الكود بحالة ان مافيه مهام من الاساس
    }

    node* current = head;//نوده تشير لاول مهمه 
    int currentIndex = 1;// انشاء متغير و اعطائه قيمه 1 لان المهمه الاولى بتكون برقم 1

    // البحث عن المهمة
    while (current && currentIndex < taskNumber) {// الشرط هنا عشان يبحث و يمشي اذا ماحصل رقم المهمه المطلوبه
        current = current->next;
        currentIndex++;
    }

    // التحقق مما إذا كان رقم المهمة موجودًا
    if (current && currentIndex == taskNumber) {
        // حفظ الحالة السابقة وتحديث المهمة إلى مكتملة
        current->previousState = current->iscompleted;// هنا بنحفظ حاله المهمه الاولى اللي هي فولس يعني مب مكتلمه الى البيريفوس عشان لو قرر المستخدم انه يتراجع عن قراره
        current->iscompleted = true;// تحديث حاله المهمه الى كومبليت

        setColor(10);
        cout << "\nTask " << taskNumber << " has been updated to completed:\n";
        cout << "Task: " << current->task << " [Priority " << current->priority << "]"<< "(Completed)";
        setColor(7);//  نعيد اللون الى اللون الافتراضي

        // طلب تأكيد التراجع
        cout << "\n Do you want to undo this action? (y/n): ";
        char choice;
        cin >> choice;
        if (choice == 'y' || choice == 'Y') {
            // استعادة الحالة السابقة
            current->iscompleted = current->previousState;//اذا طلب المستخدم انه يتراجع نعطي الكرنت القيمه اللي سجلناه قبل و هي انه مب مكتمله 
            setColor(14);
            cout << "Action undone\n";
            setColor(7);
        } else if (choice == 'n' || choice == 'N') {
            // المستخدم اختار عدم التراجع
            setColor(11);
            cout << "Task marked as completed.\n";
            setColor(7);
        } else {
            // إدخال غير صالح
            setColor(12);
            cout << "Invalid input ,No changes were made.\n";
            setColor(7);
        }
    } else {
        // رقم المهمة غير موجود
        setColor(12);
        cout << "Task number " << taskNumber << " does not exist,Please try again.\n";
        setColor(7);
    }
}


//-------------------------------------------------(البحث ( رند
void ToDoList::search_task(int taskNumber) {
    node* current = head;
    int currentIndex = 1; // يحسب رقم المهمه الى يبحث عنها

    while (current != NULL) {
        if (currentIndex == taskNumber) {//اذا رقم المهمه ليست الاولويه= الرقم المدخل
            cout << "Task found:\n";
            cout << "Task: " << current->task << " [Priority " << current->priority << "]";
            if (current->iscompleted) {
                setColor(10);
                cout << " (Completed)";
                setColor(7);
            } else {
                setColor(12);
                cout << " (Not Completed)";
                setColor(7);
            }
            cout << endl;
            return;
        }
        current = current->next;// ينتقل للنود الى بعده
        currentIndex++;
    }
    setColor(12);
    cout << "Task not found.\n";
    setColor(7);
}

//-------------------------------------------------(الحذف ( ارين
void ToDoList::remove_task(int taskNumber) {
    if (head==NULL) {
        cout << "No tasks to remove.\n";
        return;
    }

    node* current = head;
    node* previous = NULL;
    int currentIndex = 1;

    // البحث عن المهمة باستخدام رقمها
    while (current != NULL && currentIndex < taskNumber) {
        previous = current;
        current = current->next;
        currentIndex++;
    }

    // إذا كانت المهمة موجودة
    if (current != NULL && currentIndex == taskNumber) {
        if (previous == NULL) {
            // إذا كانت المهمة هي المهمة الأولى
            head = current->next;
        } else {
            // إذا كانت المهمة ليست الأولى
            previous->next = current->next;
        }

        setColor(10);
        cout << "\nTask removed successfully:\n";
        cout << "Task: " << current->task << " [Priority " << current->priority << "]\n";
        setColor(7);

        delete current; // تحرير الذاكرة
    } else {
        setColor(12);
        cout << "Task not found.\n";
        setColor(7);
    }
}

// عداد المهام
int ToDoList::count_tasks() {
    int count = 0;
    node* current = head;
    while (current != NULL) {
        count++;
        current = current->next;
    }
    return count;
}

// البرنامج الرئيسي
int main() {
    ToDoList ob;
    int choice;

    do {
        cout << "\n===================================\n";
        cout << "         To-Do List Menu\n";
        cout << "===================================\n";
        cout << "1. Add Task\n";
        cout << "2. Delete Task\n";
        cout << "3. Update Task\n";
        cout << "4. Search Task\n";
        cout << "5. Display Tasks\n";
        cout << "6. Exit\n";
        cout << "====================================================================\n";
        cout << "You have " << ob.count_tasks() << " tasks.\n";
        cout << "Enter your choice: ";
        cin >> choice;
         
       // range 1-6 غيره يطلب من المستخدم يدخل مره اخرى
        while(choice > 6){ 
            cout << "wrong choice ,try again: "<<endl;
            cout << "Enter your choice: ";
        cin >> choice;
        break;
        }

        switch (choice) {
            
        case 1: {
            string tk;
            int pro;
            cout << "Enter the task name: ";
            cin.ignore();// يتغاضى المسافات 
            getline(cin, tk);//(يقرا المهمه من المستخدم) يقرا السطر كامل لين اضغط انتر
            cout << "Enter task priority: ";
            cin >> pro;// يقرا الاولويه من المستخدم
            ob.add_task(pro, tk);
            break;
        }

        case 2: {
                   int taskNumber;
                   cout << "Enter the number of the task you want to remove: ";
                   cin >> taskNumber;
                   ob.remove_task(taskNumber);
                   break;
}

        case 3: { 
             cout << "Here are the current tasks:\n";
             ob.display_task();

            int taskNumber;
            cout << "Enter the number of the task you want to mark as completed: ";
            cin >> taskNumber;
            ob.update_task(taskNumber);
            break;
        }
        
        case 4: {
            int taskNumber;
            cout << "Enter the task number for search: ";
            cin >> taskNumber;
            ob.search_task(taskNumber);
            break;
        }

        case 5:ob.display_task();break;

        case 6:cout << "Exiting the program.\n";break;

        default:
            setColor(12);
            cout << "Invalid choice! Please try again.\n";
            setColor(7);
            break;
        }

    } while (choice != 6);

    return 0;
}
