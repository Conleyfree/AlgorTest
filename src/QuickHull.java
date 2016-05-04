import java.util.*;

public class QuickHull {
	Map<Integer, HullNode> minHull = new HashMap<Integer, HullNode>();
	
	public static void main(String[] args){

		//固定实例测试：
		ArrayList<HullNode> hulls = new ArrayList<HullNode>();

		hulls.add(new HullNode(0, 0));
		hulls.add(new HullNode(1, 6));
		hulls.add(new HullNode(2, 3));
		hulls.add(new HullNode(5, 8));
		hulls.add(new HullNode(6, 2));
		hulls.add(new HullNode(10, 9));
		hulls.add(new HullNode(13, 15));
		hulls.add(new HullNode(15, 20));
		hulls.add(new HullNode(18, 16));

		System.out.println("求以下点的凸包：");
		for(HullNode node : hulls){
			System.out.print(node.id + " :( " + node.x + " , " + node.y + " ); ");
		}
		
		QuickHull q = new QuickHull();
		q.sortNodes(hulls);
		q.quickHull(hulls, hulls.get(0), hulls.get(hulls.size()-1), 0);
		System.out.println("\n快包算法结果是：");
		q.showResult();

		//随机实例
		Hull hull = Hull.getHull();
		hull.randomAddSomeNode(10);
		hull.show();
		hull.showResult();

	}

	public void sortNodes(ArrayList<HullNode> hulls){
		//按结点横坐标升序排序
		for(int i = 1; i < hulls.size(); i++){
			for(int j = i - 1; j >= 0; j--){
				if(hulls.get(j + 1).x < hulls.get(j).x) {
					HullNode temp = hulls.get(j + 1);
					hulls.set(j + 1, hulls.get(j));
					hulls.set(j, temp);
				}else {
					break;
				}
			}
		}
	}
	
	public void quickHull(ArrayList<HullNode> hulls,HullNode head, HullNode end, int mark){
		/*
		调用该方法的前置条件是hulls的节点已经按x的值递增排好序
		mark等于0是代表第一次进入上下包都要求，
		mark=1是代表求head与end连线上包离线最远的点，该点一定为上包顶点，
		mark=-1代表求head与end连线下包离线最远的点，该点一定为下包顶点
		head与end包含在hulls里面，hulls的长度大于等于2
		 */

		if(hulls.size() == 2){
			minHull.put(head.id, head);
			minHull.put(end.id, end);
			return ;
		}
		if(mark == 0){
			minHull.put(head.id, head);
			minHull.put(end.id, end);
		}

		//分别找出head与end连线上包和下包集合与上下包离线最远的点
		ArrayList<HullNode> upperHulls = new ArrayList<HullNode>();
		ArrayList<HullNode> lowerHulls = new ArrayList<HullNode>();
		upperHulls.add(head);
		lowerHulls.add(head);
		HullNode upperMax = null;		//上包离head和end连线最远的点,（底相等）距离越远围成的面积越大
		HullNode lowerMax = null;		//下包离head和end连线最远的点
		double upperMaxL = 0, lowerMaxL = 0;
		for(HullNode node : hulls){
			double halfS = isLeft(node, head, end);
			if(halfS > 0){
				upperHulls.add(node);
				if(halfS > upperMaxL){
					upperMax = node;
					upperMaxL = halfS;
				}
			}else{
				if(halfS < 0){
					lowerHulls.add(node);
					if(halfS < lowerMaxL){		//halfS为负数
						lowerMaxL = halfS;
						lowerMax = node;
					}
				}
				//在P1Pmax连线上的点不用再去考虑
			}
		}
		upperHulls.add(end);
		lowerHulls.add(end);

		if(mark >= 0){
			if(upperHulls.size() > 2){	//需要继续向上找
				if(upperMax != null) {	//理论上>2就不可能等于空，为了安全
					minHull.put(upperMax.id, upperMax);		//最远点必为凸包顶点
					quickHull(upperHulls, head, upperMax, 1);
					quickHull(upperHulls, upperMax, end, 1);
				}
			}else {
				//相当于上包集合为空，两个端点一定是凸包的端点
				minHull.put(head.id, head);
				minHull.put(end.id, end);
			}

		}
		if(mark <= 0){
			if(lowerHulls.size() > 2){	//需要继续向下找
				if(lowerMax != null){
					minHull.put(lowerMax.id, lowerMax);
					quickHull(lowerHulls, head, lowerMax, -1);
					quickHull(lowerHulls, lowerMax, end, -1);
				}
			}else {
				//相当于上包集合为空，两个端点一定是凸包的端点
				minHull.put(head.id, head);
				minHull.put(end.id, end);
			}
		}
	}
	
	public double isLeft(HullNode node, HullNode head, HullNode end){
		//判断传入的第一个HullNode，是否在第二、三个参数连线的左侧
		//返回值的绝对值的1/2为三个点围成三角形的面积
		return (head.x * end.y + node.x * head.y + end.x * node.y - node.x * end.y - end.x * head.y - head.x * node.y);
	}

	public void showResult(){
		if(minHull.isEmpty()){
			System.out.println("空包的凸包为空！");
		}else{
			Collection<HullNode> minHullValues = minHull.values();
			System.out.print("[ ");
			for(HullNode value : minHullValues){
				System.out.print( value.id + ":(" + value.x + " , " + value.y + "); ");
			}
			System.out.println("]");
		}
	}
}

class Hull{		//凸包集合

	private ArrayList<HullNode> hulls = new ArrayList<HullNode>();
	private Random r = new Random(System.currentTimeMillis());
	private volatile static Hull hull;

	private Hull(){
		//首先把固定实例的数据清空
		HullNode.length = 0;
	}

	//为了程序的稳定只能设为单例模式
	public static Hull getHull(){
		if(hull == null){
			synchronized (Hull.class){
				if(hull == null){
					hull = new Hull();
					return hull;
				}
			}
		}
		return hull;
	}

	//单个增加
	public void randomAddANode(){
		HullNode node = new HullNode(r.nextInt(100), r.nextInt(100));
		hulls.add(node);
	}

	//批量增加
	public void randomAddSomeNode(int count){
		while(count-- > 0){
			randomAddANode();
		}
	}

	public void show(){
		System.out.println("求以下点的凸包：");
		for(HullNode node : hulls){
			System.out.print(node.id + " :( " + node.x + " , " + node.y + " ); ");
		}
	}

	public void showResult(){
		QuickHull q = new QuickHull();
		q.sortNodes(hulls);
		System.out.print("\n按结点横坐标升序排序");
		show();
		System.out.println("\n快包算法结果是：");
		q.quickHull(hulls,hulls.get(0), hulls.get(hulls.size()-1), 0);
		q.showResult();
	}
}