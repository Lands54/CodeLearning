import cmath
import math


class Impedance:
    def __init__(self, real=0, imag=0):
        self.value = complex(real, imag)

    def capacitance_impedance(self, capacitance, frequency):
        omega = 2 * cmath.pi * frequency
        self.value = complex(0, -1 / (omega * capacitance))
        return self

    def inductance_impedance(self, inductance, frequency):
        omega = 2 * cmath.pi * frequency
        self.value = complex(0, omega * inductance)
        return self

    def resistance_impedance(self, resistance, frequency):
        self.value = complex(resistance, 0)
        return self

    def __add__(self, other):
        # 串联阻抗运算
        return Impedance(self.value + other.value, 0)

    def __truediv__(self, other):
        # 并联阻抗运算
        if other.value == 0:
            raise ValueError("Division by zero (parallel impedance with zero impedance).")
        return Impedance(1 / (1 / self.value + 1 / other.value), 0)

    def __repr__(self):
        return f"{self.value.real} + j{self.value.imag}"


f = 10.7e6
X_1 = Impedance().capacitance_impedance(120e-12, f)
X_2 = Impedance().resistance_impedance(50, f)
X_3 = Impedance().capacitance_impedance(10e-9, f)
X_4 = Impedance().resistance_impedance(150, f)
X_5 = Impedance().inductance_impedance(3.3e-6, f)
X_6 = Impedance().inductance_impedance(210e-9, f)
X_7 = Impedance().resistance_impedance(22, f)
X_8 = Impedance().resistance_impedance(68, f)
X_9 = Impedance().resistance_impedance(68, f)
Z_L = X_2 + ((X_6 + X_7) / (X_3 + (X_8) / (X_4 + (X_9 / Impedance().resistance_impedance(50, f)))))
X_10 = Impedance().capacitance_impedance(120e-12, f)
X_11 = Impedance().capacitance_impedance(120e-12, f)
X_12 = Impedance().inductance_impedance(2.2e-6, f)
Z_LL = Impedance().resistance_impedance(380, f) + (X_5 / (X_1 + Z_L))
A1 = ((Z_LL.value - Impedance().resistance_impedance(380, f).value) / Z_LL.value)
A2 = A1 * Z_L.value /(X_11 + Z_L).value
print(20 * math.log10(abs(A2)))