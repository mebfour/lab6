import ev3dev2.motor as motor
import time
from ev3dev2.motor import LargeMotor, OUTPUT_A, OUTPUT_D
import math
import time
from math import pi

filename = "lab_4.txt"
f = open(filename, 'w')

motorA = motor.LargeMotor(motor.OUTPUT_A)
motorD = motor.LargeMotor(motor.OUTPUT_D)

T = 0.05
rad = 23 / 1000
base = 0.168

def normalize_angle(angle):
    import ev3dev2.motor as motor
import time
from ev3dev2.motor import LargeMotor, OUTPUT_A, OUTPUT_D
import math
import time
from math import pi

filename = "lab_4.txt"
f = open(filename, 'w')

motorA = motor.LargeMotor(motor.OUTPUT_A)
motorD = motor.LargeMotor(motor.OUTPUT_D)

T = 0.05
rad = 23 / 1000
base = 0.168

def normalize_angle(angle):
    return math.atan2(math.sin(angle), math.cos(angle))
class Odometry:
    def __init__(self, r, B, T):
        self.wheel_rad = r
        self.base = B
        self.T = T

        self.x_integrator = Integrator(0, T)
        self.y_integrator = Integrator(0, T)
        self.theta_integrator = Integrator(0, T)

        self.x = 0
        self.y = 0
        self.theta = 0

    def get_speed(self, wl: float, wr: float, th: float) -> tuple:
        v = (wr + wl) * (rad) / 2

        w = (wr - wl) * (rad) / base

        dx = v * math.cos(th)
        dy = v * math.sin(th)
        dth = w

        return (dx, dy, dth)

    def update(self, wl: float, wr: float) -> tuple:
        dx, dy, dth = self.get_speed(wl, wr, self.theta)

        self.x = self.x_integrator.update(dx)
        self.y = self.y_integrator.update(dy)
        self.theta = self.theta_integrator.update(dth)

        return (self.x, self.y, self.theta)


class Integrator:
    def __init__(self, x0: float, T: float):
        self.x0 = x0
        self.x1 = 0
        self.T = T
        self.integral = x0

    def update(self, val: float) -> float:
        self.integral += (self.x1 + val) * self.T / 2
        self.x1 = val
        return self.integral


def get_error(xg, yg, x, y, th) -> tuple:
    ex = xg - x
    ey = yg - y

    rho = math.sqrt(ex ** 2 + ey ** 2)

    psi = math.atan2(ey, ex)

    alpha = normalize_angle(normalize_angle(psi - th))

    return (rho, alpha)


def calc_control(rho, alpha) -> tuple:
    ks = 220
    kr = 200

    v_goal = ks * rho
    w_goal = kr * alpha

    return (v_goal, w_goal)


def saturation(u) -> float:
    return max(min(u, 40), -40)


k = 1
target_points = [(1*k, 0), (1*k, 1*k), (-1*k, 1*k), (-1*k, -1*k), (1*k, -1*k), (0, 0)]

current_target_index = 0

tachka = Odometry(r=rad, B=base, T=T)
try:
    while current_target_index < len(target_points):
        x_target, y_target = target_points[current_target_index]
        target_reached = False

        while not target_reached:
            t1 = time.time()
            wl_fact = motorA.speed * math.pi / 180
            wr_fact = motorD.speed * math.pi / 180
            x, y, theta = tachka.update(wl_fact, wr_fact)

            how_long, alpha = get_error(x_target, y_target, tachka.x, tachka.y, tachka.theta)
            v_goal, w_goal = calc_control(how_long, alpha)
            ur = saturation(v_goal + w_goal)
            ul = saturation(v_goal - w_goal)
            motorA.run_direct(duty_cycle_sp=ul)
            motorD.run_direct(duty_cycle_sp=ur)



            t2 = time.time()
            if (t2 - t1) < T:
                time.sleep(T - (t2 - t1))
            else:
                print("neto")

            
            f.write(str(x) + " " + str(y) + " " + str(motorA.speed) + " " + str(motorD.speed) + " " + str(how_long) + " " + str(alpha) + " " + str(theta)+ "\n")

            if how_long < 0.082:
                motorA.run_direct(duty_cycle_sp=0)
                motorD.run_direct(duty_cycle_sp=0)
                time.sleep(2)
                print("new dot")
                print("x=%.3f, y=%.3f, theta_deg=%.1f" % (tachka.x, tachka.y, math.degrees(tachka.theta)))
                break

        current_target_index += 1
finally:
    motorA.run_direct(duty_cycle_sp=0)
    motorD.run_direct(duty_cycle_sp=0)
    f.close()
class Odometry:
    def __init__(self, r, B, T):
        self.wheel_rad = r
        self.base = B
        self.T = T

        self.x_integrator = Integrator(0, T)
        self.y_integrator = Integrator(0, T)
        self.theta_integrator = Integrator(0, T)

        self.x = 0
        self.y = 0
        self.theta = 0

    def get_speed(self, wl: float, wr: float, th: float) -> tuple:
        v = (wr + wl) * (rad) / 2

        w = (wr - wl) * (rad) / base

        dx = v * math.cos(th)
        dy = v * math.sin(th)
        dth = w

        return (dx, dy, dth)

    def update(self, wl: float, wr: float) -> tuple:
        dx, dy, dth = self.get_speed(wl, wr, self.theta)

        self.x = self.x_integrator.update(dx)
        self.y = self.y_integrator.update(dy)
        self.theta = self.theta_integrator.update(dth)

        return (self.x, self.y, self.theta)


class Integrator:
    def __init__(self, x0: float, T: float):
        self.x0 = x0
        self.x1 = 0
        self.T = T
        self.integral = x0

    def update(self, val: float) -> float:
        self.integral += (self.x1 + val) * self.T / 2
        self.x1 = val
        return self.integral


def get_error(xg, yg, x, y, th) -> tuple:
    ex = xg - x
    ey = yg - y

    rho = math.sqrt(ex ** 2 + ey ** 2)

    psi = math.atan2(ey, ex)

    alpha = normalize_angle(normalize_angle(psi - th))

    return (rho, alpha)


def calc_control(rho, alpha) -> tuple:
    ks = 200
    kr = 170

    v_goal = ks * rho
    w_goal = kr * alpha

    return (v_goal, w_goal)


def saturation(u) -> float:
    return max(min(u, 40), -40)


k = 1
target_points = [(1*k, 0), (1*k, 1*k), (-1*k, 1*k), (-1*k, -1*k), (1*k, -1*k), (0, 0)]

current_target_index = 0

tachka = Odometry(r=rad, B=base, T=T)
try:
    while current_target_index < len(target_points):
        x_target, y_target = target_points[current_target_index]
        target_reached = False

        while not target_reached:
            t1 = time.time()
            wl_fact = motorA.speed * math.pi / 180
            wr_fact = motorD.speed * math.pi / 180
            x, y, theta = tachka.update(wl_fact, wr_fact)

            how_long, alpha = get_error(x_target, y_target, tachka.x, tachka.y, tachka.theta)
            v_goal, w_goal = calc_control(how_long, alpha)
            ur = saturation(v_goal + w_goal)
            ul = saturation(v_goal - w_goal)
            motorA.run_direct(duty_cycle_sp=ul)
            motorD.run_direct(duty_cycle_sp=ur)



            t2 = time.time()
            if (t2 - t1) < T:
                time.sleep(T - (t2 - t1))
            else:
                print("neto")

            
            f.write(str(x) + " " + str(y) + " " + str(motorA.speed) + " " + str(motorD.speed) + " " + str(how_long) + str(alpha)+ str(theta)+ "\n")

            if how_long < 0.09:
                motorA.run_direct(duty_cycle_sp=0)
                motorD.run_direct(duty_cycle_sp=0)
                time.sleep(2)
                print("new dot")
                print("x=%.3f, y=%.3f, theta_deg=%.1f" % (tachka.x, tachka.y, math.degrees(tachka.theta)))
                break

        current_target_index += 1
finally:
    motorA.run_direct(duty_cycle_sp=0)
    motorD.run_direct(duty_cycle_sp=0)
    f.close()