from selenium import webdriver
from selenium.webdriver.common.by import By
from time import sleep
sleep(3)
account_number = "21012217"
password_number = "19891113Qq@@"
site = webdriver.Chrome("D:\EnvironMent\webdriver\chromedriver.exe")
site.get("http://10.21.221.98/a79.htm")
side=site.find_element(By.XPATH,"/html/body/div/div/div[3]/div[1]/div/div[2]/div[1]/div/select/option[2]")
side.click()
account=site.find_element(By.XPATH,"/html/body/div/div/div[3]/div[1]/div/div[2]/div[1]/div/form/input[2]")
account.send_keys(account_number)
password=site.find_element(By.XPATH,"/html/body/div/div/div[3]/div[1]/div/div[2]/div[1]/div/form/input[3]")
password.send_keys(password_number,"\n")
site.get("https://lgn.bjut.edu.cn/")
account=site.find_element(By.XPATH,"/html/body/div/div/div[2]/form/table/tbody/tr[5]/td[2]/input")
account.send_keys(account_number)
password=site.find_element(By.XPATH,"/html/body/div/div/div[2]/form/table/tbody/tr[6]/td[2]/input")
password.send_keys(password_number,"\n")
site.quit()


