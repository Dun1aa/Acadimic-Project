import sqlite3#مكتبه الي بنشا منه ملف قاعده البيانات وبعض العمليات الي اقدر اسويه
import tkinter as tk
from tkinter import messagebox
#!!!!!بارت 4
import webbrowser# عشان يفتح لي احداثيات المسجد بقوقل ماب
from difflib import get_close_matches #لو  اخطا المستخدم بالاسم يقترح اقرب نتيجه


class MosqDB:
    def __init__(self, db_name="mosques.db"): # تشتغل أول ما انشا كائن من الكلاس

        # نسوي اتصال بملف قاعدة بيانات اسمه mosques.dbولو مهب موجود بننشاه
        #ونخزن الاتصال بهالمتغير
        self.conn = sqlite3.connect(db_name)
        # نأخذ كائن pointer عشان ننفذ أوامر sqlاضافه وحذف وتحديث
        self.pointer = self.conn.cursor()

        # ننشئ جدول المساجد لو مهب موجود من قبل
        #وكتبت فيه امر sqlوحطيت الاعمده
        self.pointer.execute("""
            CREATE TABLE IF NOT EXISTS Mosq(
                ID INTEGER PRIMARY KEY,
                Name TEXT NOT NULL,
                Type TEXT,
                Address TEXT,
                Coordinates TEXT,
                Imam_name TEXT
            )
        """)
        # احفظ التغييرات
        self.conn.commit()

    def Disp(self):#تجيب كل السجلات من جدول Mosq
        self.pointer.execute("SELECT * FROM Mosq")#اشغل امر ال sql
        return self.pointer.fetchall()#وترجعه ع شكل قائمه

    def Search(self, name):#تبحث عن مسجد واحد بالاسم
       #؟ هو القيمه الي اعطيناه
       #name,علشان  راح يستقبل مجموعه قيم التيوبل
        self.pointer.execute("SELECT * FROM Mosq WHERE Name = ?", (name,))
        #حسب المطلوب ترجع one record"
        return self.pointer.fetchone()

    def Insert(self, ID, name, typ, address, coordinates, Imam_name):#تاخذ بيانات المسجد وتضيفه للجدول
        self.pointer.execute(
            "INSERT INTO Mosq(ID, Name, Type, Address, Coordinates, Imam_name) VALUES (?, ?, ?, ?, ?, ?)",
            (ID, name, typ, address, coordinates, Imam_name)
        )
        self.conn.commit()#حفظ التغييرات

    def Delete(self, ID):#تحذف سجل المسجد حسب الايدي حقه
        self.pointer.execute("DELETE FROM Mosq WHERE ID = ?", (ID,))
        self.conn.commit()

    def UpdateImam(self, ID, new_imam):#احدث اسم الامام بناء على الاي دي
        self.pointer.execute(
            "UPDATE Mosq SET Imam_name = ? WHERE ID = ?",
            (new_imam, ID)
        )#بتاخذ رقم المسجد و اسم الامام الجديد وتنفذ الامر
        self.conn.commit()#تحفظ التغييرات

    def __del__(self):#داله الاغلاق وهنا نقفل الاتصال بالقاعده
        # نتاكد ان فيه اتصال قبل نقفله
        try:
            self.conn.close()
        except:
            pass
#-----------------------------------------------------------------------------------------------------------------خلصت من قاعده البيانات------

class MosqC:#الان الكلاس المسؤول عنالواجهه
    def __init__(self, root):
        # نخزن نافذة tkinter
        self.root = root
        self.root.title("Mosques Management System")#عنوان النافذه

        # انشات كائن من قاعدة البيانات الي زينته
        self.db = MosqDB()

        #  اسوي حقول الإدخال 1
        self.entry()
        #عرض السجلات 2
        self.lisbox()
        self.buttons()
# ==================================بارت1

    def entry(self):#هنا بنشأ اول بارت بالواجهه

        # إطار (Frame) خاص ببيانات المسجد
        self.input_frame = tk.LabelFrame(self.root, text="Mosque Data", padx=10, pady=10)
        # نحطه في الصف 0، العمود 0 فوق يسار
        self.input_frame.grid(row=0, column=0, sticky="nw", padx=10, pady=10)

        # ====== الصف الأول لل ID ======
        tk.Label(self.input_frame, text="ID:").grid(row=0, column=0, sticky="e", pady=2)
        self.entry_id = tk.Entry(self.input_frame, width=30)#خزنته ككائن بالكلاس عشان اقدر استخمه داخل الكلاس كله
        self.entry_id.grid(row=0, column=1, pady=2)

        # ====== الصف الثاني لل Name ======
        tk.Label(self.input_frame, text="Name:").grid(row=1, column=0, sticky="e", pady=2)
        self.entry_name = tk.Entry(self.input_frame, width=30)
        self.entry_name.grid(row=1, column=1, pady=2)

        # ====== الصف الثالث لل Type (Typ) ======
        tk.Label(self.input_frame, text="Type:").grid(row=2, column=0, sticky="e", pady=2)
        self.entry_typ = tk.Entry(self.input_frame, width=30)
        self.entry_typ.grid(row=2, column=1, pady=2)

        # ====== الصف الرابع لل Address ======
        tk.Label(self.input_frame, text="Address:").grid(row=3, column=0, sticky="e", pady=2)
        self.entry_address = tk.Entry(self.input_frame, width=30)
        self.entry_address.grid(row=3, column=1, pady=2)

        # ====== الصف الخامس لل Coordinates ======
        tk.Label(self.input_frame, text="Coordinates:").grid(row=4, column=0, sticky="e", pady=2)
        self.entry_coordinates = tk.Entry(self.input_frame, width=30)
        self.entry_coordinates.grid(row=4, column=1, pady=2)

        # ====== الصف السادس لل Imam Name ======
        tk.Label(self.input_frame, text="Imam Name:").grid(row=5, column=0, sticky="e", pady=2)
        self.entry_imam = tk.Entry(self.input_frame, width=30)
        self.entry_imam.grid(row=5, column=1, pady=2)

#==================================بارت2

    def lisbox(self):#اسوي الليست بوكس الي بعرض به سجلات المساجد
        # إطار خاص بقائمة السجلات
        self.lis_f = tk.LabelFrame(self.root, text="Mosques Records", padx=10, pady=10)
        # بحطه يمين Part 1 (صف 0، عمود 1)
        self.lis_f.grid(row=0, column=1, sticky="nw", padx=10, pady=10)

        # الـ listbox نفسه
        self.listbox = tk.Listbox(self.lis_f, width=60, height=15)
        self.listbox.grid(row=0, column=0, sticky="ns")
        #شريط تمرير عشان لو صار عندي سجلات كثيره
        scrollbar = tk.Scrollbar(self.lis_f, orient="vertical", command=self.listbox.yview)
        scrollbar.grid(row=0, column=1, sticky="ns")

        # نربط الـ ListBox بالـ Scrollbar
        self.listbox.config(yscrollcommand=scrollbar.set)

    def show_all(self):#بتجيب كل السجلات من قاعده البيانات وتعرضه بالليست بوكس

        self.listbox.delete(0, tk.END) # نفرغ اللي داخل ListBox قبل التعبئة
        rows = self.db.Disp()  # نجيب البيانات من الداتا بيس

        for row in rows:  # نعرض كل سجل كسطر داخل الـ ListBox
            self.listbox.insert(tk.END, row)

    def clear(self):#تمسح لي كل حقول الادخال
        self.entry_id.delete(0, tk.END)
        self.entry_name.delete(0, tk.END)
        self.entry_typ.delete(0, tk.END)
        self.entry_address.delete(0, tk.END)
        self.entry_coordinates.delete(0, tk.END)
        self.entry_imam.delete(0, tk.END)

    def add(self):#بتقرالي القيم من الحقول وبتضيف لي مسجد جديد للقاعده
        # اقرا النص من كل حقل واشيل المسافات الزيادة
        id_txt = self.entry_id.get().strip()
        name = self.entry_name.get().strip()
        typ = self.entry_typ.get().strip()
        addres = self.entry_address.get().strip()
        cordinate = self.entry_coordinates.get().strip()
        imam = self.entry_imam.get().strip()

        # التحقق لازمid و name يكونون مو فاضين
        if not id_txt or not name:
            messagebox.showwarning("mising data", "you shold write the id and name")
            return

        # احول ال id  لرقم صحيح
        try:
            id_val = int(id_txt)
        except ValueError:
            messagebox.showerror("invalid id", "id must be an integer.")
            return

        #  اضيف السجل في قاعدة البيانات
        try:
            self.db.Insert(id_val, name, typ, addres, cordinate, imam)
        except sqlite3.IntegrityError:
            # يصير هذا لو الـ ID مكرر (لأنه PRIMARY KEY)
            messagebox.showerror("Error", "This id is exists.")
            return

        # لو كل شيء تمام
        messagebox.showinfo("done", "added successfully.")

        # احدث ال listbox وانظف الحقول
        self.show_all()
        self.clear()

    def search(self):#ابحث عن المسجد بالاسم
        name = self.entry_name.get().strip()#بتجيب اسم المسجد الي انكتب
        # إذا الاسم فاضي
        if not name:
            messagebox.showwarning("missing data", "enter the name to search")
            return
        #اخذ نتيجه البحث منقاعده البيانات
        row = self.db.Search(name)

        # افرغ ال الليست بوكس قبل يعرض النتيجه
        self.listbox.delete(0, tk.END)

        # !!!!!عدلت عليه عشان بارت 4

        if row is None:#لو البحث مالقى نتيجه راح يستخدم سمارت سيرتش
            all_rows = self.db.Disp()  # نجيب كل السجلات الموجودة
            all_names = [r[1] for r in all_rows]  # ناخذ بس أسماء المساجد
            suggestions= get_close_matches(name, all_names, n=5, cutoff=0.3)#بتبحث عن اقرب اسم يشبه له ونسبه التشابه المطلوبه من صفر لين 1
            if suggestions:
                # سويت قائمة نصوص مفصولة بسطر جديد عشان لو كان به اكثر من اقتراح تحسين بس زياده
                suggestions_text = " , ".join(suggestions)
                messagebox.showinfo("Did you mean?", f"Did you mean: {suggestions_text} ?")
            else:
                messagebox.showinfo("not found", "ther is no mosque with this name") # ما لقى شيء

            return

        # لو حصل سجل يعرضه في ليست بوكس
        self.listbox.insert(tk.END, row)

        # يعبي الحقول من السجل اللي رجع يعني يفكك السجل لمتغيرات
        mosq_id, mosq_name, mosq_typ, mosq_addres, mosq_cordinate, mosq_imam = row

        self.entry_id.delete(0, tk.END)#امسح محتواه القديم
        self.entry_id.insert(0, mosq_id)#واعبيه بالبيانات الجديده

        self.entry_name.delete(0, tk.END)
        self.entry_name.insert(0, mosq_name)

        self.entry_typ.delete(0, tk.END)
        self.entry_typ.insert(0, mosq_typ or "")

        self.entry_address.delete(0, tk.END)
        self.entry_address.insert(0, mosq_addres or "")

        self.entry_coordinates.delete(0, tk.END)
        self.entry_coordinates.insert(0, mosq_cordinate or "")

        self.entry_imam.delete(0, tk.END)
        self.entry_imam.insert(0, mosq_imam or "")

    def delete(self):#تحذف المسجد بناء على الاي دي
        id_txt = self.entry_id.get().strip()#تاخذ الاي دي المدخل

        if not id_txt:#اذا مادخل شي
            messagebox.showwarning("mising data", "enter the id to delete")
            return

        try:#هل هو رقم صحيح او لا
            id_val = int(id_txt)
        except ValueError:
            messagebox.showerror("invalid id", "id shold be a number")
            return

        # تأكيد من المستخدم قبل مايحذف
        if not messagebox.askyesno("confirm Delete", f"Are you sure you want to delete mosque ID {id_val}?"):
            return

        # ننفذ الحذف
        self.db.Delete(id_val)
        messagebox.showinfo("deleted", "it was deleted.")
        # يمسح الحقول ويحدث القائمة
        self.clear()
        self.show_all()

    def update(self):#بيحدث لي اسم امام المسجد بعد ماقرا الاي دي

        id_txt = self.entry_id.get().strip()#ياخذ الاي دي
        new_imam = self.entry_imam.get().strip()#اسم الامام الجديد

        # نتحقق من أن الحقول المهمة مو فاضية
        if not id_txt or not new_imam:
            messagebox.showwarning("missing data", "id and imam Name are required")
            return

        try:#يحول الاي دي من نص لرقم
            id_val = int(id_txt)
        except ValueError:
            messagebox.showerror("invalid id", "id must be a number")
            return

        # نتاكد من المستخدم قبل الحذف
        if not messagebox.askyesno("confirm update", f"update imam name for mosque ID {id_val}?"):
            return

        # ننفذ التحديث في قاعدة البيانات
        self.db.UpdateImam(id_val, new_imam)

        messagebox.showinfo("updated", "imam name successfully")

        # نحدّث  الليست بوكس عشان نشوف التغيير
        self.show_all()
#!!!!!بارت 4
    def show_map(self):#تفتح موقع المسجد  بقوقل ماب حسب الاحداثيات بالحقل
        cordinate = self.entry_coordinates.get().strip()

        # لو الإحداثيات فاضية
        if not cordinate:
            messagebox.showwarning("missing data", "enter the coordinates ")
            return
        #  ننشأ رابط قوقل ماب ويحفظه دلخل المتغير
        url = f"https://www.google.com/maps/search/?api=1&query={cordinate}"

        #  نفتح الرابط في المتصفح
        try:
            webbrowser.open(url)
        except Exception as e:
            messagebox.showerror("Error", f"Could not open map.\n{e}")


#==================================بارت3

    def buttons(self):#انشا الازرار لكل عمليه

        self.btn_frame = tk.Frame(self.root)
        self.btn_frame.grid(row=1, column=0, sticky="w", padx=10, pady=10)

        # زر الإضافة
        self.btn_add = tk.Button(self.btn_frame,text="Add Entry",width=20,command=self.add)
        self.btn_add.grid(row=0, column=0, padx=5, pady=3)
        # زر عرض الكل
        self.btn_display = tk.Button(#انشا زر جديد
            self.btn_frame,#عشان يحطه داخل الفريم
            text="Display All",
            width=20,
            command=self.show_all  #يعني اذا ضغط الزر نفذ الداله
        )
        self.btn_display.grid(row=0, column=1, padx=5, pady=3)
        # زر البحث
        self.btn_search = tk.Button(self.btn_frame,text="Search",width=20,command=self.search )
        self.btn_search.grid(row=1, column=0, padx=5, pady=3)
        # زر الحذف
        self.btn_delete = tk.Button(self.btn_frame,text="Delete",width=20,command=self.delete)
        self.btn_delete.grid(row=1, column=1, padx=5, pady=3)
        # زر تنظيف الحقول
        self.btn_clear = tk.Button(self.btn_frame,text="Clear",width=20,command=self.clear)
        self.btn_clear.grid(row=2, column=0, pady=3)
        #زر تحديث اسم الإمام
        self.btn_update = tk.Button(self.btn_frame,text="Update Imam",width=20,command=self.update)
        self.btn_update.grid(row=2, column=1, pady=3, padx=5)
        # !!!!!بارت 4
        # زر عرض المسجد على الخريطة
        self.btn_map = tk.Button(self.btn_frame, text="Show Map",width=20, command=self.show_map)
        self.btn_map.grid(row=3, column=0, pady=3, padx=5)



if __name__ == "__main__":
    root = tk.Tk()#ينشا لي نافذه البرنامج
    app = MosqC(root)#ينشا لي اوبجكت
    root.mainloop()#يشغل لي النافذه وينتظر شغل المستخدم