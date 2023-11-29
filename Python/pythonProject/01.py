import collections
import random
class Dealer:
    def __init__(self, max_num):
        self.max_num = max_num
        self.record_list = []
        self.unknow_pos = []
        self.unknow_n=[]
        self.unknow_t=[]
        self.save_reg = [0, 0]
        self.pos = 0
        self.pos_2 = 0
        self.mod = 0
        self.be_pos = [0,0]
        self.d = True

    def do(self, num_pair):
        if self.mod == 0:
            self.desicion(num_pair)
        elif self.mod != 0:
            self.end_decision(num_pair)

    def desicion(self, num_pair):
        if self.eq_deal(num_pair):
            self.unknow_pos.append(len(self.record_list))
            self.record_list.append([0])
        elif self.common_deal(num_pair) != -1:
            self.record_list.append(self.common_deal(num_pair))
        self.save_reg = num_pair
        if self.pos == 2 * self.max_num - 1:
            self.mod += 1
            self.expand_record()

    def expand_record(self):
        for _ in range(2 * self.max_num - len(self.record_list)):
            self.record_list.append([0])
        for index in range(len(self.record_list)):
            if self.record_list[index] == [0] and index not in self.unknow_pos:
                self.unknow_pos.append(index)
        self.mod += 1

    def act(self):
        if self.pos >= 2 * self.max_num - 1:
            return self.end_act()
        else:
            return self.commen_act()

    def commen_act(self):
        self.pos += 1
        self.be_pos = [self.pos, self.pos + 1]
        return self.pos, self.pos + 1

    def end_act(self):
        if self.pos_2 < len(self.unknow_pos) -1:
            # print(self.unknow_pos[0])
            self.be_pos = [self.unknow_pos[self.pos_2] + 1, self.unknow_pos[self.pos_2 + 1] + 1]
            self.pos_2 += 1
            self.d = True
        else:
            if self.unknow_t[0] == 1:
                self.be_pos[0] = self.record_list.index(self.unknow_n[0]) + 1
                self.be_pos[1] = self.unknow_pos[random.randint(0,len(self.unknow_pos) - 1)] + 1
            else:
                self.be_pos[0] = self.unknow_pos[random.randint(0, len(self.unknow_pos) - 1)] + 1
                self.be_pos[1] = self.unknow_pos[random.randint(0, len(self.unknow_pos) - 1)] + 1
                while self.be_pos[0] == self.be_pos[1]:
                    self.be_pos[0] = self.unknow_pos[random.randint(0, len(self.unknow_pos) - 1)] + 1
                    self.be_pos[1] = self.unknow_pos[random.randint(0, len(self.unknow_pos) - 1)] + 1
        return self.be_pos[0], self.be_pos[1]

    def common_deal(self, num_pair):
        if self.save_reg[0] in num_pair:
            return self.save_reg[1]
        elif self.save_reg[1] in num_pair:
            return self.save_reg[0]
        else:
            return -1

    def end_decision(self, num_pair):
        if self.d:
            for i in range(2):
                if num_pair[i] not in self.unknow_n:
                    self.unknow_n.append(num_pair[i])
                    if num_pair[i] in self.record_list:
                        self.unknow_t.append(1)
                    else:
                        self.unknow_t.append(0)
            self.d = False

        elif num_pair[0] == num_pair[1]:
            self.record_list[self.be_pos[0] - 1] = num_pair[0]
            self.record_list[self.be_pos[1] - 1] = num_pair[1]
            for i in range(2):
                if (self.be_pos[i] - 1) in self.unknow_pos:
                    self.unknow_pos.remove(self.be_pos[i] - 1)
            self.unknow_t.pop(self.unknow_n.index(num_pair[0]))
            self.unknow_n.remove(num_pair[0])

    def eq_deal(self, num_pair):
        if (num_pair[0] in self.save_reg and num_pair[1] in self.save_reg and
                self.save_reg[0] in num_pair and self.save_reg[1] in num_pair):
            return 1
        else:
            return 0

    def comti_dect(self):
        if self.unknow_pos == [] and self.mod > 1:
            return False
        else:
            return True


if __name__ == '__main__':
    x = int(input())
    dm = Dealer(int(x/2))
    while True:
        if dm.comti_dect():
            pos = dm.act()
            print("? "+str(pos[0]),str(pos[1]))
            i = str(input(""))
            ip = [int(x) for x in i.split()]
            dm.do(ip)
            # print(dm.record_list)
            # print(dm.unknow_n)
            # print(dm.unknow_t)
            # print(dm.unknow_pos)
        else:
            print("!",end=' ')
            for e in dm.record_list:
                print(e,end=' ')
            break

