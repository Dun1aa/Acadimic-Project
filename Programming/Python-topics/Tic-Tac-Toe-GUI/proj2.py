
import tkinter as tk
from tkinter import messagebox

corner = [1, 3, 7, 9]#الزوايا

position = {
  1: (0, 0), 2: (0, 1), 3: (0, 2),
  4: (1, 0), 5: (1, 1), 6: (1, 2),
  7: (2, 0), 8: (2, 1), 9: (2, 2)}

#احتمالات الفوز عشان اتحقق من الفوز
#فوز صف-عمود-اقطار
win_set = [
    (1, 2, 3), (4, 5, 6), (7, 8, 9),
    (1, 4, 7), (2, 5, 8), (3, 6, 9),
    (1, 5, 9), (3, 5, 7)
]
s= [2, 4, 6, 8]#الوسط بالصف او العمود عشان اقدر اخلي الكمبيتر يختاره

class TicTacToe:
    #الكونستركتور
    def __init__(self, root):
        self.root = root
        root.title("Tic-Tac-Toe")#عنوان نافذتي
        self.bord = {i: ' ' for i in range(1, 10)}#انشات قاموس به 9 فراغات بالبدايه عشان اخزن فيه العلامات الي اختاره اللاعب او الكمبيوتر
        self.buttons = {}#قاموس عشان بخزن فيه الازرار الي بتمثل المربعات بالواجهه

         #اخزن به خيارات اللاعب(ضد مين بيلعب ,اي رمز بيختار,مين الي بيبدا)
        self.mode_var = tk.StringVar(value="cmp")   #(cmp) أو(p2)و افتراضي حطيته ضد الكمبيوتر
        self.sym_var = tk.StringVar(value="X")   # (X)أو(O)حطيت قيمه افتراضيه اكس
        self.start_var = tk.StringVar(value="yes")  # (yes)أو(no)

        self.build_c()
        self.bld_bord()


      #تبني الجزء العلوي باللعبه (زر اختبار اللاعب و اختيار الرمز وزر بدايه اللعبهو اختيار مين يبدا)
    def build_c(self):
        frm = tk.Frame(self.root)#انشات ايطار زي jpanel
        frm.grid(row=0, column=0, sticky="w")
# النصوص الي بتظهر للمستخدم بتكون جهه اليسار وكل نص بصف
        tk.Label(frm, text="Play With").grid(row=0, column=0, sticky="w")
        tk.Label(frm, text="Select ").grid(row=1, column=0, sticky="w")
        tk.Label(frm, text="Start the game").grid(row=2, column=0, sticky="w")

        # زر الاختيار للعب مع مين.......
        tk.Radiobutton(frm, text="Compter", variable=self.mode_var, value="cmp").grid(row=0, column=1, sticky="w")
        tk.Radiobutton(frm, text="Player 2", variable=self.mode_var, value="p2").grid(row=0, column=2, sticky="w")
        #ازار اختيار الرمز للعب.......
        tk.Radiobutton(frm, text="X", variable=self.sym_var, value="X").grid(row=1, column=1, sticky="w")
        tk.Radiobutton(frm, text="O", variable=self.sym_var, value="O").grid(row=1, column=2, sticky="w")
        #ازرار الاختيار اذا بيبدا يلعب او لا......
        tk.Radiobutton(frm, text="Yes", variable=self.start_var, value="yes").grid(row=2, column=1, sticky="w")
        tk.Radiobutton(frm, text="No", variable=self.start_var, value="no").grid(row=2, column=2, sticky="w")

        tk.Button(frm, text="Start", command=self.start_game, width=20).grid(row=3, column=1)
#_______________________________
    #تبني شكبه 3في3 للازرار الي بيلعبون به
    def bld_bord(self):
        b_frame = tk.Frame(self.root)
        b_frame.grid(row=1, column=0)#تكون تحت عناصر الاختيار العلويه الي كانت بالصف 0
        #لوب بتمر على كل عناصر القاموس الي قبل سويته وبترجع الكي (الرقم)مع صفه وعموده
        for pos, (r, c) in position.items():
            btn = tk.Button(b_frame, text=" ", width=8, height=4,command=lambda p=pos: self.cel_click(p))
            btn.grid(row=r, column=c, sticky="nsew")#بيحدد موقع الزر حسب الصف و العمود
            self.buttons[pos] = btn#اخزنه داخل القاموس الي سويته قبل بالكونستركتور عشان اقدر اوصل للزر بعدين بسهوله

# _______________________________
    def reset_brd(self):
        self.bord = {i: ' ' for i in range(1, 10)}#افرغ اللوحه
        for b in self.buttons.values():#بيمر ع كل ازرار اللوحه ويفرغ النص
            b.config(text=" ", state="normal")#اغير حالته تكون قابله للضغط
# تحقق_______________________________
    def is_free(self, pos):# اذا خانه pos فاضيهبيكونt
        return self.bord[pos] == ' '
# _______________________________
    def avilable_mov(self):#يجمع كل الخانات الفاضيهويحطه بقائمه
        return [p for p in range(1, 10) if self.is_free(p)]
# احط العلامه باللوحه_______________________________
    def place_move(self, pos, mrk):
        self.bord[pos] = mrk
        # تحدّث الزر في الواجهة وتكتب عليه الرمز

        self.buttons[pos].config(text=mrk)
        #-------حذفه------- self.buttons[pos].config(text=mrk, state="disabled")#اغير حالته امنع الضغط عليه
# _______________________________التحقق من الفوز
    def check_winner(self, mrk):  # اخليه تمر على كل مجموعه الفوز
        return any(all(self.bord[p] == mrk for p in combo) for combo in win_set)
# _______________________________اذا كان فيه تعادل
    def is_draw(self):
        return all(self.bord[p] != ' ' for p in range(1, 10))
# _______________________________
    # بخلي لعب الكمبيوتر اذكى حسب تكنيكات اللعب
    #يعني اخليه يحاول يفوز ويمنع اللاعب من الفوز
    #و يختار الوسط لان هو الي يزيد احتماليه الفوز
    #وياخذ الزاويه لو الوسط موموجوداو ياخذ الجوانب
    def smart_mov(self, cmp_mrk, human_mrk):

        for pos in self.avilable_mov():
            if self.w_win(pos, cmp_mrk):#لو فيه حركه بتفوزه ياخذه
                return pos
        # امنع اللاعب من الفوز
        for pos in self.avilable_mov():
            if self.w_win(pos, human_mrk):#لو فيه حركه تمنع الخصم يفوز ياخذه
                return pos
        # خذ الوسطيه
        if 5 in self.avilable_mov():
            return 5
         #ياخذ زاوبه
        for pos in corner:#بيمر ع القائمه ويدور اول زايه فاضيهي ياخذه
            if pos in self.avilable_mov():
                return pos
        #بياخذ الي بالجوانب 2-4-6-8
        for pos in s:
            if pos in self.avilable_mov():
                return pos
        return self.avilable_mov()[0]

    def w_win(self, pos, mrk):# بتساعده بالفوز
        temp = self.bord.copy()#اخذ نسخه من اللوحه عشان ما ااثر بالاصليه
        temp[pos] = mrk
        return any(all(temp[p] == mrk for p in combo) for combo in win_set)#هل فيه فوز؟بتطلعt

    #من هنا بدايه اللعبه او الجوله****************--
    def start_game(self):
        self.reset_brd()#يسوي ريست للوحه
        self.human_mrk = self.sym_var.get()#اخذ الرمز الي اختاره
        self.cmp_mrk = 'O' if self.human_mrk == 'X' else 'X'
        self.turn = "human" if self.start_var.get() == "yes" else "opponent"
        self.vs_cmp = (self.mode_var.get() == "cmp")#يرجع ترو اذا كان يلعب ضد كمبيوتر
        if self.turn == "opponent" and self.vs_cmp:#اذا كان دور الخصم و الخصم هو الكمبيوتر اخليه يبدا يلعب
            self.cmp_play()

    #اذا ضعط زر من ازرار اللعبه راح تتنفذ هالداله
    def cel_click(self, pos):
       #تحديد الرمز الي بنحطه على حسب مين دوره الان
       mrk = self.human_mrk if self.turn == "human" else self.cmp_mrk
       if not self.is_free(pos):# احدد اذا الخانه فاضيه او مشغوله
            messagebox.showwarning("Invalid", "الخانة مشغولة.")
            return
       self.place_move(pos, mrk)#هالداله تحدث الزر في الواجهه
       self.after_move(mrk)#بتتاكد لي اذا به فوز او تعادل و اذا انتهت اللعبه او لا
       if self.vs_cmp and self.turn == "opponent" and not self.game_over():#نلعب ضد الكمبيوتر و الدور صار دور الكمبيوتر و اللعبه ما انتهت
            self.root.after(150, self.cmp_play)#اخلي الكمبيوتر ياعب بهالسرعه

    def cmp_play(self):#داله حركه الكمبيوتر او لعبه
        pos = self.smart_mov(self.cmp_mrk, self.human_mrk)#بيختار افضل خانه
        self.place_move(pos, self.cmp_mrk)#بتاخذ رمز الكمبيوتر في الخانه الي رجعت من سمارت موف
        self.after_move(self.cmp_mrk)#نفس الي مع اللاعب تفحص الفوز و التعادل

    def after_move(self, mark_just_played):#وش يصير بعد كل حركه
        #تفحص لي الفوز و التعادل وتعرض النتيجه
        if self.check_winner(mark_just_played):#تاخذ الرمز الي انلعب فيه تو في الحركه الاخيره
            who = "الكمبيوتر" if (self.vs_cmp and mark_just_played == self.cmp_mrk) else "اللاعب"
            self.end_game(f"الفائز: {who} ({mark_just_played})")
            return
        if self.is_draw():
            self.end_game("تعادل!")
            return
        # تبديل الدور دام اللعبه مستمره
        self.turn = "opponent" if self.turn == "human" else "human"

    def game_over(self):#ان اللعبه خلاص منتهيه في التعادل او الفوز
        return self.is_draw() or self.check_winner(self.human_mrk) or self.check_winner(self.cmp_mrk)


    def end_game(self, result_text):#بنهي الجوله وسواله اذا بيلعب مره ثانيه او لا
        for b in self.buttons.values():#بيمر ع كل الازار بالقاموس ويغير حالته
            b.config(state="disabled")
        messagebox.showinfo("نتيجة اللعبة", result_text)
        # نسأل المستخدم إذا حاب يلعب جولة أخرى
        if messagebox.askyesno("Play Again", "هل تريد لعب جولة أخرى؟"):
            # هنا ما نبدأ لعبة جديدة مباشرة
            # 1) بفرغ حالة اللوحة في القاموس
            self.bord = {i: ' ' for i in range(1, 10)}

            # 2) بفرغ النصوص من الأزرار وتكون مقفله
            #    إلى أن يضغط المستخدم على زر Start من فوق
            for b in self.buttons.values():
                b.config(text=" ", state="disabled")

def main():
    root = tk.Tk()#يسوي لي نافذه اساسيه
    app = TicTacToe(root)#ينشا لي كائن ويمرر له النافذه
    root.mainloop()#بيخلي البرنامج شغال ويستقبل ضغطات المستخدم لين تتسكر النافذه

if __name__ == "__main__":proj2.py
    main()
