import java.util.*;

public class BayouClient{

    private Random rn = new Random();

    public static void main(String[] args){
	System.out.println("WELCOME...");
	System.out.println("TO JURASSIC PARK");
    }

    private void addRandomSong(){
	
    }

    private void removeRandomSong(){
	
    }

    private void randomModifyRandomSong(){
	
    }

    private String getRandomTitle(){
	String title = "" + rn.nextInt();
	switch (rn.nextInt() % 3) {
	case 0: title += " Reasons I Love You"; break;
	case 1: title += " Problems and malloc Ain't One"; break;
	case 2: title += " Red Balloons"; break;
	case 3: title = "Fur Elise"; break;
	}
	return title;
    }

    private String getRandomURL(){
	return "http://www.cs.utexas.edu/~lorenzo/mySongs/" + rn.nextInt() + ".mp3";
    }

    public void addSong(Object song){}
    public void removeSong(Object song){}
    public void modifySong(Object song){}

    public void breakOurSystem(){
	
    }

    private void generatePlaylist(){
	
    }

    public BayouClient(){
	
    }
}
