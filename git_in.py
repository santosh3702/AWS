#This script is used to install fresh git or it also used to update the existing git
import os
import sys
import platform
import time
import re
import urllib
from subprocess import *
os.system('clear')
print("Please wait checking the pre-requisites.....")
time.sleep(2)

def welcome():
    print"*****************************************************"
    print"This script will find the existing git version if any\nand also update/install latest git"
    print"*****************************************************"
    time.sleep(4)

def Thank_you():
    print "\n\nThank you for using this Script\nHave a great day\n"
    return None

def run_cmd_status(cmd):
    sp=Popen(cmd,shell=True,stdout=PIPE,stderr=PIPE)
    rt=sp.wait()
    out,err=sp.communicate()
    return out

def is_root():
    if os.getuid()==0:
        return True
    else:
        return False
try:
   if is_root()==True:
       print"you are the root user. so you can run this script"
   else:
        print"Please run this script as a root user"
        Thank_you()
        sys.exit(1)
except Exception as e:
    print e
    print"Please rectify the erro and try it"
    sys.exit(2)
try:
    #print"Checking wget command"
    wget_status=run_cmd_status('wget --version')
    if wget_status=='':
        #print"out is none"
        print"Please wait installing wget command"
        run_cmd_status("yum install wget -y")
    else:
        print"satiesfied with wget command"
except Exception as e:
    print e
    print"Please rectify the error and try it"
    Thank_you()
    sys.exit(3)
try:
    #print"Checking pip command"
    pip_status=pip_status=run_cmd_status('pip --version')
    if pip_status=='':
        print "Please wiat installing pip command"
        run_cmd_status("easy_install pip")
    else:
        print"Satiesfied with pip command"
except Exception as e:
    print e
    print"Please rectify the error and try it"
    Thank_you()
    sys.exit(4)
try:
    try:
        from bs4 import BeautifulSoup
        print"imported BeautifulSoup sucessfully"
    except Exception as e:
        print"Please wait installing the BeautifulSoup (bs4)"
        run_cmd_status('pip install bs4')
	from bs4 import BeautifulSoup
except Exception as e:
     print e
     print"Please rectify the error and try it"
     Thank_you()
     sys.exit(5)



def get_current_git_version():
    sp=Popen('git --version',shell=True,stdout=PIPE,stderr=PIPE)
    rt=sp.wait()
    out,err=sp.communicate()
    if rt==0:
        if platform.system()=="Windows":
            return out.strip().strip(".windows.1").split()[-1]
        else:
            return out.split()[-1]
    else:
        if "not found" in err:
            return None
        print err*5+2
        sys.exit(2)
def all_git_versions():
    link="https://mirrors.edge.kernel.org/pub/software/scm/git/"
    page=urllib.urlopen(link)
    html_doc=page.read()
    page.close()
    soup=BeautifulSoup(html_doc,'html.parser')
    my_tar_ob=re.compile("git-\d\.\d+\.\d+\.tar\.gz")
    my_ver_ob=re.compile("\d\.\d+\.\d")
    href_link=[]
    ver_li=[]
    for each in soup.find_all('a',href=True):
        my_tar=each.get("href")
        if my_tar_ob.search(my_tar)!=None:
            #print my_tar
             href_link.append(link+my_tar)
             if my_ver_ob.search(my_tar)!=None:
                #print my_ver_ob.search(my_tar).group()
                ver_li.append(my_ver_ob.search(my_tar).group())
    return href_link,ver_li
def select_version(git_ver,ver_li):
    while True:
        display_versions(git_ver,ver_li)
        print"\nEnter required version from above list:"
        usr_ver=raw_input()
        if usr_ver not in ver_li:
           continue
        else:
            return usr_ver

def display_versions(git_ver,ver_li):
    Flag=False
    if git_ver not in ver_li:
        for each in ver_li:
            print each,"\t",
    else:
        for each in ver_li:
            if each==git_ver:
                Flag=True
                continue
            if Flag:
                print each,"\t",
    return None
def run_cmd(cmd):
    sp=Popen(cmd,shell=True,stdout=PIPE,stderr=PIPE)
    rt=sp.wait()
    if rt==0:
       return "Sucess"
    else:
       out,err=sp.communicate()
       print err
       print "Please rectify the error and try it"
       Thank_you()
       sys.exit(4)

def install_required_packages():
    cmd1="yum install curl-devel expat-devel gettext-devel openssl-devel zlib-devel -y"
    cmd2="yum install gcc perl-ExtUtils-MakeMaker -y"
    cmd3="yum install wget -y"
    print"Please wiat installing required packages"
    print "(curl-devel expat-devel gettext-devel openssl-devel zlib-devel and gcc)...."
    run_cmd(cmd1)
    run_cmd(cmd2)
    run_cmd(cmd3)
    time.sleep(4)
    return None
def get_current_working_dir():
    sp=Popen("pwd",shell=True,stdout=PIPE,stderr=PIPE)
    rt=sp.wait()
    out,err=sp.communicate()
    if rt==0:
       #print"out is: ",out
       return out.strip('\n').strip()
    else:
       print err
       print "please rectify it and try again"
       Thank_you()
       sys.exit(5)

def install_git(href_link,usr_ver):
    install_required_packages()
    for each in href_link:
        if usr_ver in each:
            url=each
            break
    download_tar_gz="wget "+url
    run_cmd(download_tar_gz)
    tar_ball=url.split(os.sep)[-1]
    cmd2="tar -xzf "+tar_ball
    run_cmd(cmd2)
    cmd3=tar_ball.rstrip(".tar.gz")
    pwd=get_current_working_dir()
    cmd4=pwd+os.sep+cmd3
    os.chdir(cmd4)
    os.system("./configure")
    os.system("make")
    os.system("make install")
    #os.system("ln -s /usr/local/bin/git /bin/git")   

def new_install_git():
    href_link,ver_li=all_git_versions()
    usr_ver=select_version(None,ver_li)
    install_git(href_link,usr_ver)
def update_git(git_ver):
    href_link,ver_li=all_git_versions()
    #print"all links are: ",href_link
    #print"all giversion are: ",ver_li
    usr_ver=select_version(git_ver,ver_li)
    #print"your requried ver is: ",usr_ver
    install_git(href_link,usr_ver)
def user_request():
    while True:
        print"Enter only yes/no"
        yes_or_no=raw_input()
        if yes_or_no.lower() in ['yes','no']:
            if yes_or_no.lower()=="yes":
                return "yes"
            else:
                Thank_you()
                sys.exit()

def main():
    git_ver=get_current_git_version()
    if git_ver==None:
        print "Git is not installed\n"
        print "Do you want to install git on this host?"
        yes_or_no=user_request()
        new_install_git()
	print"Now your git is installed with versioon of {}".format(get_current_git_version())
	Thank_you()
        
    else:
        print "Currently intalled git version is: {}".format(git_ver)
        print "\nDo you want to update the git on this host?"
        yes_or_no=user_request()
        update_git(git_ver)
        print"Now your latest git is: ",get_current_git_version()
        Thank_you()

if __name__=="__main__":
    time.sleep(2)
    print"Pre-requisites are statiesfied"
    time.sleep(4)
    os.system('clear')
    welcome()
    main()




