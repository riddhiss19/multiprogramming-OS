import java.io.*;
import java.lang.*;
class OS1 {
    public int R[] = new int[4];
    public int IR[] = new int[4];
    public int IC;
    public boolean C;
    int SI;
    public byte[][] M = new byte[100][4];
    FileInputStream fp = null;
    FileOutputStream fp2;
    File file = new File("input-1.txt");
    FileReader fr = new FileReader(file);
    BufferedReader br = new BufferedReader(fr);

    OS1() throws FileNotFoundException {
    }


    public final void MOS(int loc) throws IOException {
        if (SI == '1') {
            getData(loc);
        } else if (SI == '2') {
            printData(loc);
        }
    }

    public final void execute() throws IOException {
        int loc;
        fp2 = new FileOutputStream("output-1.txt");
        while (M[IC][0] != 'H') {
            loc = (M[IC][2] - '0') * 10 + M[IC][3] - '0';
            if (M[IC][0] == 'G' && M[IC][1] == 'D') {
                loc = loc - loc % 10; //GD works on a block so pass start of a block
                //   getData(loc);
                SI = (byte) '1';
                MOS(loc);
            } else if (M[IC][0] == 'P' && M[IC][1] == 'D') {
                loc = loc - loc % 10; //PD works on a block so pass start of a block
                //   printData(loc);
                SI = (byte) '2';
                MOS(loc);
            } else if (M[IC][0] == 'L' && M[IC][1] == 'R') {
                loadRegister(loc);
            } else if (M[IC][0] == 'S' && M[IC][1] == 'R') {
                storeRegister(loc);
            } else if (M[IC][0] == 'C' && M[IC][1] == 'R') {
                compareRegister(loc);
                System.out.println(IC);
            } else if (M[IC][0] == 'B' && M[IC][1] == 'T') {
                if (C == true) {
                    IC = loc;
                    continue;
                }
            }

            IC += 1;


        }
    }

    public void Load() throws IOException {
        String word = "";
        String cont1 = "$AMJ";
        String cont2 = "$DTA";
        String cont3 = "$END";

        int found;
        int c;

        while ((word = br.readLine()) != null) {

            if (word.contains(cont1)) {

                //Read Next Word and Load into memory
                word = br.readLine();
                int l = word.length();
                for (int i = 0; i < l; i++)
                {
                    int temp = i;
                    M[i / 4][i % 4] = (byte) word.charAt(temp);
                    }
                }




            else if (word.contains(cont2)) {
            //Start Execution
            IC = 0;
            execute();
        } else if (word.contains(cont3)) {
            //cout<<"\n\n";
            fp2.write("\n\n".getBytes());
            fp2.close();
            break;
        }

    }

}
    public void getData(int loc) throws IOException {
        String word;
        word = br.readLine();
        int l = word.length();
        for (int i = 0;i <= l;i++)
        {
            if (i != l)
            {
                M[loc + i / 4][i % 4] = (byte) word.charAt(i);
            }

        }
    }
    public void printData(int loc) throws IOException {
        int flag = 0;
        for (int i = 0;i < 10;i++)
        {
            for (int j = 0;j < 4;j++)
            {
                if (M[loc + i][j] != '\0')
                {
                    fp2.write(M[loc+i][j]);
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

        fp2.write("\n\n".getBytes());
    }
    public void loadRegister(int loc)
    {
        for (int i = 0;i < 4;i++)
        {
            R[i] = M[loc][i];
        }
    }
    public void storeRegister(int loc)
    {
        for (int i = 0;i < 4;i++)
        {
            M[loc][i] = (byte) R[i];
        }
    }
    public void compareRegister(int loc)
    {
        int f = 0;
        for (int i = 0;i < 4;i++)
        {
            if (M[loc][i] == R[i])
            {
                continue;
            }

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
}



public class Phase_1 {
    public static void main(String[] args) throws IOException {
        OS1 obj = new OS1();

        for (int i = 0; i < 6; i++)
        {
            for (i = 0; i < 100; i++) {
                for (int j = 0; j < 4; j++) {
                    obj.M[i][j] = ' ';
                }
            }
            obj.Load();

            for (i = 0; i < 100; i++) {
                System.out.print((i));
                System.out.print("->");
                for (int j = 0; j < 4; j++) {
                    System.out.print((char) obj.M[i][j]);
                    System.out.print(" ");
                }
                System.out.print("\n");
                if ((i + 1) % 10 == 0) {
                    System.out.print("\n\n");
                }
            }
        }
    }
    }
