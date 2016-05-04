public class HullNode {
	int x;	//x坐标
	int y;	//y坐标
	int id;		//初始化序号
	static int length = 0;	//所在包节点个数
	HullNode next;
	
	public HullNode(int x, int y){
		this.x = x;
		this.y = y;
		length++;
		id = length - 1;
	}
}
