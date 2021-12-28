import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
public class BengaliTransliterator {
	//Creating a hashmap from the csv file in ScriptChart for Bengali to Roman letters.
	private HashMap<String, String> table;
	
	public static void main(String[] args) {
		BengaliTransliterator bl = new BengaliTransliterator("BengaliChart.csv");
		bl.transliterate("LordsPrayer.txt");
	}
	
	public BengaliTransliterator(String fileName) {
		table = makeDictionary("BengaliChart.csv");
	}
	
	public HashMap<String, String> makeDictionary(String fileName) {
		Scanner sc;
		HashMap<String,String> map = new HashMap<String, String>();

		try {
			sc = new Scanner(new File(fileName));

			while(sc.hasNextLine()){
				String line = sc.nextLine();
				String[] keyVal = line.split(",");
				map.put(keyVal[0], keyVal[1]);
			}
			return map;
		} catch (IOException e) {
			System.err.println("File could not be opened");
			return null;
		}
	}
	//Function returning the output of transliterateWord on each word in the file line by line
	public void transliterate(String fileName) {
		try {
			String bengLine = "Bengali: ";
			String transLine = "Transliteration: ";
			Scanner sc = new Scanner(new File(fileName));
			while(sc.hasNextLine()) {
				String line = sc.nextLine();
				String[] words = line.split(" ");
				for(int i = 0; i < words.length; i++) {
					bengLine+=words[i] + " ";
					String transWord = transliterateWord(words[i]);
					transLine+=transWord + " ";
				}
				bengLine+="\n";
				transLine+= "\n";
	
			}
			System.out.println(bengLine);
			System.out.println(transLine);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	//Returns transliterated word from bengali word
	public String transliterateWord(String word) {
		String english = "";
		int length =  word.length();
		int i = 0;
		while(i < length) {
			char ch = word.charAt(i);
			int ucode = (int) ch;
			String hcode = new String("0x" + Integer.toHexString(ucode).toUpperCase());
			if(isLetter(ucode)) {
				String transLetter = (String) table.get(hcode);
				//Checks if letter is at the end
				if(i + 1 < length) {
					char nextCh = word.charAt(i+1);
					int nextUCode = (int) nextCh;
					String nextHCode = new String("0x" + Integer.toHexString(nextUCode).toUpperCase());
					//Checks next letter to see if the inherent vowel should be added or not
					if(isLetter(nextUCode)) {
						if(isVowel(ucode)) {
							english+=transLetter;
							i+=1;
						} else {
							english+= transLetter + 'ô';
							i+=1;
						}
					} else if(isMatra(nextUCode)) {
						String nextTransLetter = (String) table.get(nextHCode);
						english+= transLetter + nextTransLetter;
						if(i+2 < length) {
							char secondNextCh = word.charAt(i+2);
							int secondNextUCode = (int) secondNextCh;
							String secondNextHCode = new String("0x" + Integer.toHexString(secondNextUCode).toUpperCase());
							if(isAyogvah(secondNextUCode)) {
								String secondNextTransLetter = (String) table.get(secondNextHCode);
								english+=secondNextTransLetter;
								i+=2;
							} else {
								i+=2;
							}
						} else {
							i+=1;
						}
					} else if(isAyogvah(nextUCode)) {
						String nextTransLetter = (String) table.get(nextHCode);
						english+= transLetter + 'ô' + nextTransLetter;
						i+=2;
					} else if(isHoshonto(nextUCode)) {
						if(i + 2 < length) {
							english+=transLetter;
							i+=2;
						} else {
							english+=transLetter;
							i+=1;
						}
					} else if(isNukta(nextUCode)) {
						if(ucode == 2465) {
							english += "ṛ";
							if(i+2<length) {
								char secondNextCh = word.charAt(i+2);
								int secondNextUCode = (int) secondNextCh;
								String secondNextHCode = new String("0x" + Integer.toHexString(secondNextUCode).toUpperCase());
								if(isMatra(secondNextUCode)) {
									String secondNextTransLetter = (String) table.get(secondNextHCode);
									if(i + 2 < length) {
										english+=secondNextTransLetter;
										i+=2;
									} else {
										english+=secondNextTransLetter;
										i+=1;
									}
								} else {
									i+=1;
									}
							} else {
								i+=1;
							}
						} else if(ucode == 2566) {
							english += "ṛh";
							if(i+2<length) {
								char secondNextCh = word.charAt(i+2);
								int secondNextUCode = (int) secondNextCh;
								String secondNextHCode = new String("0x" + Integer.toHexString(secondNextUCode).toUpperCase());
								if(isMatra(secondNextUCode)) {
									String secondNextTransLetter = (String) table.get(secondNextHCode);
									if(i + 2 < length) {
										english+=secondNextTransLetter;
										i+=2;
									} else {
										english+=secondNextTransLetter;
										i+=1;
									}
								} else {
									i+=1;
									}
							} else {
								i+=1;
							}

						}  else {
							english += "y";
							if(i+2<length) {
								char secondNextCh = word.charAt(i+2);
								int secondNextUCode = (int) secondNextCh;
								String secondNextHCode = new String("0x" + Integer.toHexString(secondNextUCode).toUpperCase());
								if(isMatra(secondNextUCode)) {
									String secondNextTransLetter = (String) table.get(secondNextHCode);
									if(i + 2 < length) {
										english+=secondNextTransLetter;
										i+=2;
									} else {
										english+=secondNextTransLetter;
										i+=1;
									}
								} else {
									i+=1;
									}
							} else {
								i+=1;
							}
						}
					}
				}  else {
					english+=transLetter;
					i+=1;
				}
			} else {
				i+=1;
			}
		}
		return english;
	}
	//Set of functions returning if the word is in a specified class of letters or not 
	public boolean isLetter(int ucode) {
		int start = 2437;
		int end = 2489;
		return (start <= ucode && ucode <= end) || (2524 <= ucode && ucode <= 2527);
	}
	public boolean isVowel(int ucode) {
		int start = 2437;
		int end = 2452;
		return start <= ucode && ucode <= end;
	}
	public boolean isMatra(int ucode) {
		int start = 2494;
		int end = 2508;
		return start <= ucode && ucode <= end;
	}
	public boolean isHoshonto(int ucode) {
		return ucode == 2509;
	}
	public boolean isNukta(int ucode) {
		return ucode == 2492;
	}
	public boolean isAyogvah(int ucode) {
		int start = 2433;
		int end = 2435;
		return start <= ucode && ucode <= end;
 
	}
}
