import heapq
from datetime import datetime

class Flight:
# دالة في بايثون لإنشاء المتغيرات 
    def __init__(self, FlightName, Takeoff, Landing, Priority):
        self.FlightName = FlightName
        self.Takeoff = datetime.strptime(Takeoff, "%H:%M")
        self.Landing = datetime.strptime(Landing, "%H:%M")
        self.Priority = Priority
# دالة في بايثون تستخدم للمقارنة
    def __lt__(self, other):
        if self.Takeoff != other.Takeoff:
            return self.Takeoff < other.Takeoff
        if self.Priority != other.Priority:
            return self.Priority > other.Priority
        return (self.Landing - self.Takeoff) < (other.Landing - other.Takeoff)
# كلاس للأولوية
class PriorityQueue:
    def __init__(self):
        self.queue = []

   # دالة لإضافة رحلةpriorityQ
    def insert(self, flight):
        heapq.heappush(self.queue, flight)

    # دالة تحذف الرحلة إلى لها الأولوية الأعلى 
    def remove_max(self):
        return heapq.heappop(self.queue)

    # دالة لعرض الأولوية بدون حذف
    def peek(self):
        return self.queue[0] if self.queue else None

def AddFlight(priorityQ, FlightName, Takeoff, Landing, Priority):
    flight = Flight(FlightName, Takeoff, Landing, Priority)
    priorityQ.insert(flight)

def SortFlights(priorityQ):
    sorted_flights = []
    while priorityQ.queue:
        flight = priorityQ.remove_max()
        if priorityQ.queue and priorityQ.peek().Takeoff == flight.Takeoff:
            next_flight = priorityQ.remove_max()
            best_flight = flight if flight < next_flight else next_flight
            if best_flight == flight:
                priorityQ.insert(next_flight)# يرجع يضيف الرحلات ذات الآوية الأقل
            else:
                priorityQ.insert(flight)
                flight = next_flight
        sorted_flights.append(flight)
    return sorted_flights

def Display(SortedFlights):
    print("__________________________________")
    print(" Flight Schedule:")
    for flight in SortedFlights:
        print(f"Flight: {flight.FlightName}, Takeoff: {flight.Takeoff.strftime('%H:%M')}, Landing: {flight.Landing.strftime('%H:%M')}, Priority: {flight.Priority}")

priorityQ = PriorityQueue()
n = int(input("Enter number of flights: "))
for i in range(n):
    FlightName = input("Enter flight name: ")
# هنا راح يتأكد ان أسم الرحله مو فاضي
    while not FlightName:  
        print("Flight name cannot be empty.")
        FlightName = input("Enter flight name: ")
    Takeoff = input("Enter takeoff time (HH:MM): ")
    Landing = input("Enter landing time (HH:MM): ")
    Priority = int(input("Enter priority level (1 = Regular, 2 = International, 3 = Emergency, higher number is more important): "))
    AddFlight(priorityQ, FlightName, Takeoff, Landing, Priority)

SortedFlights = SortFlights(priorityQ)
Display(SortedFlights)