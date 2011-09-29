public class Map implements Defines {
	public int w,h,numDots,numBigDots;
	float startx,starty;
	char[] data;

	public Map(){
		this.w = 7;
		this.h = 7;
		this.numDots = 0;
		this.numBigDots = 0;

		data = new char[w*h];
		for(int i = 0; i < w; ++i){
			data[0+i*w] = 1;
			data[7+i*w] = 1;
			data[i+0*w] = 1;
			data[i+7*w] = 1;
		}
	}
}
