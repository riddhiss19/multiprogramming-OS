import java.io.*;
import java.lang.*;
import java.util.Arrays;
import java.util.Random;

class OS {
    Random random = new Random();
    public int R[] = new int[4];
    public byte IR[] = new byte[4];
    public int IC;
    public boolean C;
    public int VA;
    public int RA;
    public int SI;
    public int TI;
    public int PI;
    public int Jid;
    public int OD;
    public String EM;
    public int[] status = new int[30];
    public int PTR;
    public int curr;
    public static class PCB
    {
        public int TTL;
        public int TLL;
        public int TTC;
        public int LLC;
    }
    public PCB pcb = new PCB();
    public byte[][] M = new byte[300][4];

    FileInputStream fp = null;
    FileOutputStream fp2;

    File file = new File("input-2.txt");
    FileReader fr = new FileReader(file);
    BufferedReader br = new BufferedReader(fr);

    OS() throws FileNotFoundException {
    }

    public int MOS() throws IOException {
        System.out.print("STATUS:Generating Interrupt.......\n");
        if (TI == 0)
        {

            if (SI == 1)
            {
                SI = 0;
                PI = 0;
                return getData();

            } //incase of valid page fault PI=3 else block wont be executed as this is executed first
            else if (SI == 2)
            {
                SI = 0;
                PI = 0;
                return printData();

            }
            else if (SI == 3)
            {
                return terminate(0);
            }


            else if (PI == 1)
            {
                return terminate(4);
            }
            else if (PI == 2)
            {
                return terminate(5);
            }
            else if (PI == 3)
            {
                return terminate(6);
            }


        }
        else if (TI == 2)
        {
            if (SI == 1)
            {
                return terminate(3);
            }
            else if (SI == 2)
            {
                printData();
                return terminate(3);
            }
            else if (SI == 3)
            {
                return terminate(0);
            }


            else if (PI == 1)
            {
                fp2.write("Time Limit Exeeded".getBytes());
                return terminate(4);
            }
            else if (PI == 2)
            {
                fp2.write("Time Limit Exeeded".getBytes());
                return terminate(5);
            }
            else if (PI == 3)
            {
                return terminate(3);
            }

        }
        return 0;
    }

    public int terminate(int err) throws IOException {
        System.out.print("STATUS:Terminating.......\n");
        fp2.write(("JOB ID :"+Jid+"\n").getBytes());
        switch (err)
        {
            case 0:
            {
                fp2.write("NO Error \n".getBytes());
            }
            break;
            case 1:
            {
                fp2.write("Out Of Data \n".getBytes());
            }
            break;
            case 2:
            {
                fp2.write("Line Limit Exist \n".getBytes());
            }
            break;
            case 3:
            {
                fp2.write("Time Limit Exist \n".getBytes());
            }
            break;
            case 4:
            {
                fp2.write("Opration Code Error \n".getBytes());
            }
            break;
            case 5:
            {
                fp2.write("Oprand Error \n".getBytes());
            }
            break;
            case 6:
            {
                fp2.write("Invalid Page Fault \n".getBytes());
            }
        }
        String str = new String(IR);
        fp2.write(("IC     :"+ IC +"\n").getBytes());
        fp2.write(("IR      :"+ str+"\n").getBytes());
        fp2.write(("SI     :"+ SI +"\n").getBytes());
        fp2.write(("PI     :"+ PI +"\n").getBytes());
        fp2.write(("TL     :"+ TI +"\n").getBytes());
        fp2.write(("TLL     :"+ pcb.TLL +"\n").getBytes());
        fp2.write(("TTC     :"+ pcb.TTC +"\n").getBytes());
        fp2.write(("LLC     :"+ pcb.LLC +"\n").getBytes());
        fp2.write(("TTL     :"+ pcb.TTL +"\n").getBytes());
        fp2.write(("\n\n").getBytes());
        SI = 0;
        PI = 0;
        TI = 0;
        return  -1;
    }


    public final void execute() throws IOException {
        int loc;
        System.out.print("STATUS:Executing the Program.......\n");
        fp2 = new FileOutputStream("output_2.txt",true);
        //page tabel
        int RIC = addressMap(IC);
        if (PI != 0)
        {
            int res = MOS();
            if (res == -1)
                return;
        }
        do
        {
            RIC = addressMap(IC);

            for (int i = 0;i < 4;i++)
            {
                IR[i] = M[RIC][i];
            }

            if (M[RIC][0] != 'H' && (!Character.isDigit(M[RIC][2]) || !Character.isDigit(M[RIC][3])))
            {

                PI = 2;
                int res = MOS();
                if (res == -1)

                    break;

            }

            VA = (M[RIC][2] - '0') * 10 + M[RIC][3] - '0';

            RA = addressMap(VA);


            //

            if (M[RIC][0] == 'G' && M[RIC][1] == 'D')
            {

                RA = RA - RA % 10; //GD works on a block so pass start of a block
                SI = 1;


            }
            else if (M[RIC][0] == 'P' && M[RIC][1] == 'D')
            {
                RA = RA - RA % 10; //PD works on a block so pass start of a block
                SI = 2;


            }
            else if (M[RIC][0] == 'L' && M[RIC][1] == 'R')
            {
                if (PI != 3)
                {
                    loadRegister();
                }
            }
            else if (M[RIC][0] == 'S' && M[RIC][1] == 'R')
            {
                storeRegister();
            }
            else if (M[RIC][0] == 'C' && M[RIC][1] == 'R')
            {

                if (PI != 3)
                {
                    compareRegister();
                }
            }
            else if (M[RIC][0] == 'B' && M[RIC][1] == 'T')
            {
                pcb.TTC += 1;
                if (C == true)
                {
                    IC = VA;
                    continue;
                }
            }
            else if (M[RIC][0] == 'H')
            {
                pcb.TTC += 1;
                SI = 3;
            }
            else
            {
                PI = 1;
            }


            IC += 1;
            RIC += 1;

            if (SI != 0 || TI != 0 || PI != 0)
            {
                int res = MOS();
                if (res == -1)
                    break;
            }
            if (pcb.TTC > pcb.TTL)
            {
                TI = 2;
            }
            //   cout<<"LOC->"<<loc;

        }while (true);
    }

    public int allocate()
    {
        System.out.print("STATUS:Allocating Memory....\n");
        int min = 1;
        int max = 300;

        Random random = new Random();
        int randomNumber = random.nextInt((max - min) + 1) + min;

        int loc = randomNumber% 30;
        while (status[loc] == 1)
        {
            loc = (int) (Math.random() % 30);
        }
        status[loc] = 1;


        return loc * 10;
    }
    public int stringDeci(String word,int st)
    {

        return ((word.charAt(st) - '0') * 1000 + (word.charAt(st + 1) - '0') * 100 + (word.charAt(st + 2) - '0') * 10 + (word.charAt(st + 3) - '0'));

    }
    public void loadProgram(String word)
    {
        int l = word.length();
        int i = 0;
        int hcount = 0;
        System.out.print("STATUS:Loading Program in Memory.....\n");
        while (i < l)
        {
            hcount = 0;
            int st = allocate();
            insertPage(st,-1);
            int tmp = l - i;
            int cond;
            if (tmp < 40)
            {
                cond = tmp / 4 + tmp % 4;
            }

            else
            {
                cond = 10;
            }

            for (int j = st;j < (st + cond);j++)
            {
                for (int k = 0;k < 4;k++)
                {
                    M[j][k] = (byte) word.charAt(i);
                    if (word.charAt(i) == 'H')
                    {
                        M[j][1] = M[j][2] = M[j][3] = ' ';
                        i += 1;
                        break;
                    }
                    i += 1;

                }

            }
        }
    }
    public void insertPage(int loc,int ch)
    {
        String l = String.valueOf(loc / 10);
        int lc;
        lc = curr;

        M[lc][0] = ' ';
        M[lc][1] = ' ';
        if ((loc / 10) < 10)
        {
            M[lc][2] = '0';
            M[lc][3] = (byte) l.charAt(0);
        }
        else
        {
            M[lc][2] = (byte) l.charAt(0);
            M[lc][3] = (byte) l.charAt(1);
        }
        curr += 1;
    }
    public int addressMap(int VA)
    {
        int pte = PTR + VA / 10;

        System.out.print("STATUS:Mapping Virtual Address to Real Address.......\n");
        if (M[pte][2] != '*')
        {
            int rad = ((M[pte][2] - '0') * 10 + (M[pte][3] - '0')) * 10 + VA % 10;

            return rad;
        }
        else
        {
            PI = 3;
            return -1;
        }

    }



    public void Load() throws IOException {
        String word = "";
        String cont1 = "$AMJ";
        String cont2 = "$DTA";
        String cont3 = "$END";

        int found;

        while ((word = br.readLine()) != null)
        {

            if (word.contains(cont1))
            {
                PTR = allocate();
                curr = PTR;
                for (int j = PTR;j < (PTR + 10);j++)
                {
                    for (int k = 0;k < 4;k++)
                    {
                        M[j][k] = '*';
                    }
                }
                //Read Next Word and Load into memory
                pcb.TTL = stringDeci(word,8);
                pcb.TLL = stringDeci(word,12);
                Jid = stringDeci(word,4);

                word = br.readLine();
                loadProgram(word);
            }
            else if (word.contains(cont2))
            {
                //Start Execution
                IC = 0;

                execute();

            }
            else if (word.contains(cont3))
            {
                fp2.write("\n\n".getBytes());
                break;
            }

            if (OD == 1)
            {
                OD = 0;
                break;
            }

        }

    }
    public int getData() throws IOException {
        System.out.print("STATUS:Getting Data from File......\n");
        String word;
        pcb.TTC += 2;

        word = br.readLine();
        //timelimit error
        if (pcb.TTC > pcb.TTL)
        {
            return terminate(3);
        }
        //out of data
        if (word.contains("$END"))
        {
            OD = 1;
            int r = terminate(1);
            return r;
        }
        int lpg = allocate();
        insertPage(lpg,VA);
        int l = word.length();
        for (int i = 0;i <= l;i++)
        {
            if (i != l)
            {
                M[lpg + i / 4][i % 4] = (byte) word.charAt(i);
            }
            else
            {
                M[lpg + i / 4][i % 4] = '\0';
            }

        }
        return 0;

    }
    public int printData() throws IOException {
        System.out.print("STATUS:Printing Data to File....\n");
        int flag = 0;
        pcb.TTC += 1;
        pcb.LLC += 1;

        if (pcb.LLC > pcb.TLL && TI != 2)
        {
            return terminate(2);
        }
        for (int i = 0;i < 10;i++)
        {
            for (int j = 0;j < 4;j++)
            {
                if (M[RA + i][j] != '\0')
                {
                    fp2.write(M[RA + i][j]);
                }
                else
                {
                    flag = 1;
                    break;
                }
            }
            if (flag == 1)
                break;
        }

        fp2.write("\n".getBytes());
        return 0;

    }
    public void loadRegister()
    {
        System.out.print("STATUS:Loading Register....\n");
        pcb.TTC += 1;
        for (int i = 0;i < 4;i++)
        {
            R[i] = M[RA][i];
        }
    }
    public void storeRegister()
    {
        pcb.TTC += 2;
        System.out.print("STATUS:Storing Register Value in Memory.....\n");
        int lpg = allocate();
        insertPage(lpg,VA);
        for (int i = 0;i < 4;i++)
        {
            M[lpg][i] = (byte) R[i];
        }
        if (PI == 3)
        {
            PI = 0;
        }

    }
    public void compareRegister()
    {
        System.out.print("STATUS:Comparing with Register....\n");
        pcb.TTC += 1;
        int f = 0;
        for (int i = 0;i < 4;i++)
        {
            if (M[RA][i] == R[i])
                continue;
            else
            {
                f = 1;
                break;
            }
        }
        if (f == 1)
        {
            C = false;
        }
        else
        {
            C = true;
        }

    }
    public void printMem()
    {
        for (int i = 0;i < 300;i++)
        {
            System.out.print((i));
            System.out.print("->");
            for (int j = 0;j < 4;j++)
            {
                System.out.print(M[i][j]);
                System.out.print(" ");
            }
            System.out.print("\n");
            if ((i + 1) % 10 == 0)
            {
                System.out.print("\n\n");
            }
        }
        System.out.print("--------------------------------------------------\n");
    }

}



 class Phase_2 {
    public static void main(String[] args) throws IOException {
        OS obj = new OS();
        int jobs = 1;
        for (int loop = 0;loop < 7;loop++)
        {

            obj.PI = 0;
            obj.SI = 0;
            obj.TI = 0;
            obj.pcb.TTC = 0;
            obj.pcb.LLC = 0;
            for (int i = 0;i < 300;i++)
            {
                for (int j = 0;j < 4;j++)
                {
                    obj.M[i][j] = ' ';
                }
                if (i % 10 == 0)
                {
                    obj.status[i / 10] = 0;
                }
            }

            obj.OD = 0;
            obj.Load();

            for (int i = 0;i < 300;i++)
            {
                System.out.print((i));
                System.out.print("->");
                for (int j = 0;j < 4;j++)
                {
                    System.out.print((char)obj.M[i][j]);
                    System.out.print(" ");
                }
                System.out.print("\n");
                if ((i + 1) % 10 == 0)
                {
                    System.out.print("\n\n");
                }
            }
            System.out.print("--------------------------------------------------\n");

        }
    }}
