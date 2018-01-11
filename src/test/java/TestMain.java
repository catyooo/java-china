import com.javachina.kit.Utils;

import blade.kit.DateKit;

public class TestMain {

	public static void main(String[] args) {
		System.out.println(Utils.getWeight(0L, 0L, 0L, 0L, DateKit.getUnixTimeLong(DateKit.dateFormat("201604192000","yyyyMMddHHmm"))));
		System.out.println(Utils.getWeight(0L, 0L, -1L, 0L, DateKit.getUnixTimeLong(DateKit.dateFormat("201604202000","yyyyMMddHHmm"))));
		System.out.println(Utils.getWeight(10L, 2L, 0L, 0L, DateKit.getUnixTimeLong(DateKit.dateFormat("201604202000","yyyyMMddHHmm"))));
		System.out.println(Utils.getWeight(0L, 2L, 0L, 0L, DateKit.getUnixTimeLong(DateKit.dateFormat("201604202000","yyyyMMddHHmm"))));
		System.out.println(Utils.getWeight(20L, 0L, 0L, 0L, DateKit.getUnixTimeLong(DateKit.dateFormat("201604202000","yyyyMMddHHmm"))));
		System.out.println(Utils.getWeight(20L, 0L, 0L, 0L, DateKit.getUnixTimeLong(DateKit.dateFormat("201604211000","yyyyMMddHHmm"))));
		System.out.println(Utils.getWeight(2L, 10L, 0L, 0L, DateKit.getUnixTimeLong(DateKit.dateFormat("201604211000","yyyyMMddHHmm"))));
		System.out.println(Utils.getWeight(0L, 0L, 0L, 0L, DateKit.getUnixTimeLong(DateKit.dateFormat("201604211400","yyyyMMddHHmm"))));
		
	}

}
