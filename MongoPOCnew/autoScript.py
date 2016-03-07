import os;
#command = 'python Anath_code.py -w '+words +" -c "+concepts+" -e "+entities+" -o "+jsonFilename+" -i "+wordlistFile+" -u "+utter +" -t "+Test/testFile;
os.system("python Ananth_code.py -w 1000 -c 10 -e 2 -o JSONs/testJson1.json -i final.txt -u 1000 -t Test/testFile1.txt"); print("file 1 done");
os.system("python Ananth_code.py -w 1000 -c 10 -e 2 -o JSONs/testJson2.json -i final.txt -u 10000 -t Test/testFile2.txt");print("file 2 done");
# os.system("python Ananth_code.py -w 10000 -c 100 -e 10 -o JSONs/testJson3.json -i final.txt -u 50000 -t Test/testFile3.txt");print("file 3 done");
# os.system("python Ananth_code.py -w 10000 -c 100 -e 10 -o JSONs/testJson4.json -i final.txt -u 100000 -t Test/testFile4.txt");print("file 4 done");
# os.system("python Ananth_code.py -w 20000 -c 200 -e 50 -o JSONs/testJson5.json -i final.txt -u 500000 -t Test/testFile5.txt");print("file 5 done");
# os.system("python Ananth_code.py -w 40000 -c 400 -e 100 -o JSONs/testJson6.json -i final.txt -u 1000000 -t Test/testFile6.txt");print("file 6 done");
# os.system("python Ananth_code.py -w 80000 -c 800 -e 200 -o JSONs/testJson7.json -i final.txt -u 5000000 -t Test/testFile7.txt");print("file 7 done");
# os.system("python Ananth_code.py -w 100000 -c 1000 -e 400 -o JSONs/testJson8.json -i final.txt -u 5000000 -t Test/testFile8.txt");print("file 8 done");
# os.system("python Ananth_code.py -w 150000 -c 1500 -e 800 -o JSONs/testJson9.json -i final.txt -u 5000000 -t Test/testFile9.txt");print("file 9 done");
# os.system("python Ananth_code.py -w 200000 -c 2000 -e 1000 -o JSONs/testJson10.json -i final.txt -u 5000000 -t Test/testFile10.txt");print("file 10 done");
os.chdir('src')
os.system("ls");
os.system("javac -cp \"../mongo-java-driver-3.0.2.jar:../json-simple-1.1.jar\" mongopoc/mainClass.java mongopoc/makingmongo.java mongopoc/parser.java mongopoc/node.java mongopoc/jsonParsing.java");
print("this is compiled");
for i in range (1,10):
    filename = "../JSONs/testJson"+str(i);
    runCom = "java -cp \".:../mongo-java-driver-3.0.2.jar:../json-simple-1.1.jar\" mongopoc.mainClass " +filename+ " MongoDatabase5 Collection" +str(i);
    os.system(runCom);


    
java -cp ".:../mongo-java-driver-3.0.2.jar:../json-simple-1.1.jar" mongopoc.mainClass test.json MongoDatabase5 Collection1