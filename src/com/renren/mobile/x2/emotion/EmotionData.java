package com.renren.mobile.x2.emotion;

import com.renren.mobile.x2.components.home.nearbyfriends.ErrLog;


public class EmotionData {
	public static  EmotionData mInstance ;
	public static EmotionData getInstance(){
		if(mInstance == null){
			mInstance = new EmotionData();
		}
		return mInstance;
	}
	public String[] code={
			"","","","","","","","","","","","","","","",
			"","","","","","","","","","","","","","","",
			"","","","","","","","","","","","","","","",
			"","","","","","","","","","","","","","","",
			"","","",	"","","","","","","","","","","","",
			"","","",
			"","","","","","","","","","","","","","","",
			"","","","","","","","","","","","","","","",
			"","","","","","","","","","","","","","","",
			"","","","","","","","",
			"","","","","","","","","","","","","","","",
			"","","","","","","","","","","","","","","",
			"","","","","","","","","","","","","","","",
			"","","",
			"angry","awkward","bittersmile","blush","cool","crazy","crying","ghost","glad","grin","happy","mug","quiet","scared","shock",
			"shy","sleepy","smile","smirk","softly","surprise","tear","tired","vomiting",
			"ali01","ali02","ali03","ali04","ali05"
	};
	public String[] path={
			"emoji_F09F9884","emoji_F09F988A","emoji_F09F9883","emoji_E298BA","emoji_F09F9889","emoji_F09F988D","emoji_F09F9898","emoji_F09F989A","emoji_F09F98B3","emoji_F09F988C","emoji_F09F9881","emoji_F09F989C","emoji_F09F989D","emoji_F09F9892","emoji_F09F988F",
			"emoji_F09F9893","emoji_F09F9894","emoji_F09F989E","emoji_F09F9896","emoji_F09F98A5","emoji_F09F98B0","emoji_F09F98A8","emoji_F09F98A3","emoji_F09F98A2","emoji_F09F98AD","emoji_F09F9882","emoji_F09F98B2","emoji_F09F98B1","emoji_F09F98A0","emoji_F09F98A1",
			"emoji_F09F98AA","emoji_F09F98B7","emoji_F09F91BF","emoji_F09F91BD","emoji_F09F929B","emoji_F09F9299","emoji_F09F929C","emoji_F09F9297","emoji_F09F929A","emoji_E29DA4","emoji_F09F9294","emoji_F09F9293","emoji_F09F9298","emoji_E29CA8","emoji_E2AD90",
			"emoji_F09F92A2","emoji_E29D95","emoji_E29D94","emoji_F09F92A4","emoji_F09F92A8","emoji_F09F92A6","emoji_F09F8EB6","emoji_F09F8EB5","emoji_F09F94A5","emoji_F09F92A9","emoji_F09F918D","emoji_F09F918E","emoji_F09F918C","emoji_F09F918A","emoji_E29C8A",
			"emoji_E29C8C","emoji_F09F918B","emoji_E29C8B","emoji_E29880","emoji_E29894","emoji_E29881","emoji_E29B84","emoji_F09F8C99","emoji_E29AA1","emoji_F09F8C80","emoji_F09F8C8A","emoji_F09F90B1","emoji_F09F90B6","emoji_F09F90AD","emoji_F09F90B9","emoji_F09F90B0","emoji_F09F90BA","emoji_F09F90B8",
			"emoji_F09F90AF","emoji_F09F90A8","emoji_F09F90BB","emoji_F09F90B7","emoji_F09F90AE","emoji_F09F9097","emoji_F09F90B5","emoji_F09F9092","emoji_F09F90B4","emoji_F09F908E","emoji_F09F90AB","emoji_F09F9091","emoji_F09F9098","emoji_F09F908D","emoji_F09F90A6",
			"emoji_F09F90A4","emoji_F09F9094","emoji_F09F90A7","emoji_F09F909B","emoji_F09F9099","emoji_F09F90A0","emoji_F09F909F","emoji_F09F90B3","emoji_F09F90AC","emoji_F09F9290","emoji_F09F8CB8","emoji_F09F8CB7","emoji_F09F8D80","emoji_F09F8CB9","emoji_F09F8CBB",
			"emoji_F09F8CBA","emoji_F09F8D81","emoji_F09F8D83","emoji_F09F8D82","emoji_F09F8CB4","emoji_F09F8CB5","emoji_F09F8CBE","emoji_F09F909A",
			"emoji_F09F8E8D","emoji_F09F929D","emoji_F09F8E8E","emoji_F09F8E92","emoji_F09F8E93","emoji_F09F8E8F","emoji_F09F8E86","emoji_F09F8E87","emoji_F09F8E90","emoji_F09F8E91","emoji_F09F8E83","emoji_F09F91BB","emoji_F09F8E85","emoji_F09F8E84","emoji_F09F8E81",
			"emoji_F09F9494","emoji_F09F8E89","emoji_F09F8E88","emoji_F09F92BF","emoji_F09F9380","emoji_F09F93B7","emoji_F09F8EA5","emoji_F09F92BB","emoji_F09F93BA","emoji_F09F93B1","emoji_F09F93A0","emoji_E2988E","emoji_F09F92BD","emoji_F09F93BC","emoji_F09F948A",
			"emoji_F09F93A2","emoji_F09F93A3","emoji_F09F93BB","emoji_F09F93A1","emoji_E29EBF","emoji_F09F948D","emoji_F09F9493","emoji_F09F9492","emoji_F09F9491","emoji_E29C82","emoji_F09F94A8","emoji_F09F92A1","emoji_F09F93B2","emoji_F09F93A9","emoji_F09F93AB",
			"emoji_F09F93AE","emoji_F09F9B80","emoji_F09F9ABD","emoji_F09F92BA","emoji_F09F92B0","emoji_F09F94B1","emoji_F09F9AAC","emoji_F09F92A3","emoji_F09F94AB","emoji_F09F928A","emoji_F09F9289","emoji_F09F8F88","emoji_F09F8F80","emoji_E29ABD","emoji_E29ABE",
			"emoji_F09F8EBE","emoji_E29BB3","emoji_F09F8EB1",
			"angry","awkward","bittersmile","blush","cool","crazy","crying","ghost","glad","grin","happy","mug","quiet","scared","shock",
			"shy","sleepy","smile","smirk","softly","surprise","tear","tired","vomiting",
			"ali01","ali02","ali03","ali04","ali05"

	};
	public String[] name={
			"emoji_F09F9884","emoji_F09F988A","emoji_F09F9883","emoji_E298BA","emoji_F09F9889","emoji_F09F988D","emoji_F09F9898","emoji_F09F989A","emoji_F09F98B3","emoji_F09F988C","emoji_F09F9881","emoji_F09F989C","emoji_F09F989D","emoji_F09F9892","emoji_F09F988F",
			"emoji_F09F9893","emoji_F09F9894","emoji_F09F989E","emoji_F09F9896","emoji_F09F98A5","emoji_F09F98B0","emoji_F09F98A8","emoji_F09F98A3","emoji_F09F98A2","emoji_F09F98AD","emoji_F09F9882","emoji_F09F98B2","emoji_F09F98B1","emoji_F09F98A0","emoji_F09F98A1",
			"emoji_F09F98AA","emoji_F09F98B7","emoji_F09F91BF","emoji_F09F91BD","emoji_F09F929B","emoji_F09F9299","emoji_F09F929C","emoji_F09F9297","emoji_F09F929A","emoji_E29DA4","emoji_F09F9294","emoji_F09F9293","emoji_F09F9298","emoji_E29CA8","emoji_E2AD90",
			"emoji_F09F92A2","emoji_E29D95","emoji_E29D94","emoji_F09F92A4","emoji_F09F92A8","emoji_F09F92A6","emoji_F09F8EB6","emoji_F09F8EB5","emoji_F09F94A5","emoji_F09F92A9","emoji_F09F918D","emoji_F09F918E","emoji_F09F918C","emoji_F09F918A","emoji_E29C8A",
			"emoji_E29C8C","emoji_F09F918B","emoji_E29C8B","emoji_E29880","emoji_E29894","emoji_E29881","emoji_E29B84","emoji_F09F8C99","emoji_E29AA1","emoji_F09F8C80","emoji_F09F8C8A","emoji_F09F90B1","emoji_F09F90B6","emoji_F09F90AD","emoji_F09F90B9","emoji_F09F90B0","emoji_F09F90BA","emoji_F09F90B8",
			"emoji_F09F90AF","emoji_F09F90A8","emoji_F09F90BB","emoji_F09F90B7","emoji_F09F90AE","emoji_F09F9097","emoji_F09F90B5","emoji_F09F9092","emoji_F09F90B4","emoji_F09F908E","emoji_F09F90AB","emoji_F09F9091","emoji_F09F9098","emoji_F09F908D","emoji_F09F90A6",
			"emoji_F09F90A4","emoji_F09F9094","emoji_F09F90A7","emoji_F09F909B","emoji_F09F9099","emoji_F09F90A0","emoji_F09F909F","emoji_F09F90B3","emoji_F09F90AC","emoji_F09F9290","emoji_F09F8CB8","emoji_F09F8CB7","emoji_F09F8D80","emoji_F09F8CB9","emoji_F09F8CBB",
			"emoji_F09F8CBA","emoji_F09F8D81","emoji_F09F8D83","emoji_F09F8D82","emoji_F09F8CB4","emoji_F09F8CB5","emoji_F09F8CBE","emoji_F09F909A",
			"emoji_F09F8E8D","emoji_F09F929D","emoji_F09F8E8E","emoji_F09F8E92","emoji_F09F8E93","emoji_F09F8E8F","emoji_F09F8E86","emoji_F09F8E87","emoji_F09F8E90","emoji_F09F8E91","emoji_F09F8E83","emoji_F09F91BB","emoji_F09F8E85","emoji_F09F8E84","emoji_F09F8E81",
			"emoji_F09F9494","emoji_F09F8E89","emoji_F09F8E88","emoji_F09F92BF","emoji_F09F9380","emoji_F09F93B7","emoji_F09F8EA5","emoji_F09F92BB","emoji_F09F93BA","emoji_F09F93B1","emoji_F09F93A0","emoji_E2988E","emoji_F09F92BD","emoji_F09F93BC","emoji_F09F948A",
			"emoji_F09F93A2","emoji_F09F93A3","emoji_F09F93BB","emoji_F09F93A1","emoji_E29EBF","emoji_F09F948D","emoji_F09F9493","emoji_F09F9492","emoji_F09F9491","emoji_E29C82","emoji_F09F94A8","emoji_F09F92A1","emoji_F09F93B2","emoji_F09F93A9","emoji_F09F93AB",
			"emoji_F09F93AE","emoji_F09F9B80","emoji_F09F9ABD","emoji_F09F92BA","emoji_F09F92B0","emoji_F09F94B1","emoji_F09F9AAC","emoji_F09F92A3","emoji_F09F94AB","emoji_F09F928A","emoji_F09F9289","emoji_F09F8F88","emoji_F09F8F80","emoji_E29ABD","emoji_E29ABE",
			"emoji_F09F8EBE","emoji_E29BB3","emoji_F09F8EB1",
			"angry","awkward","bittersmile","blush","cool","crazy","crying","ghost","glad","grin","happy","mug","quiet","scared","shock",
			"shy","sleepy","smile","smirk","softly","surprise","tear","tired","vomiting",
			"ali01","ali02","ali03","ali04","ali05"

	};
	public int[] themeid={
			1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
			1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
			1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
			1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
			1,1,1,
			2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,
			2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,
			2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,
			2,2,2,2,2,2,2,2,
			3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,
			3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,
			3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,
			3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,
			3,3,3,
			10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,
			10,10,10,10,10,10,10,10,10,
			10,10,10,10,10

			
	};
	
	
	public void destoryInstance(){
		if(mInstance!=null){
			mInstance = null;
		}
	}
	/**********************************************************************************************************************/
	/**********************************************************************************************************************/
	/**********************************************************************************************************************/
	/**********************************************************************************************************************/
	/**************************************以下代码是所有emoji表情和所有运营表情的编码以及id**********************************************/
	/**********************************************************************************************************************/
	/**********************************************************************************************************************/
	/**********************************************************************************************************************/
	/**********************************************************************************************************************/
	/**********************************************************************************************************************/
//	public String[] code2 ={
//			"angry","awkward","bittersmile","blush","cool","crazy","crying","ghost","glad","grin","happy","mug","quiet","scared","shock",
//			"shy","sleepy","smile","smirk","softly","surprise","tear","tired","vomiting"
//	};
//	public String[] path2 = {
//			"angry","awkward","bittersmile","blush","cool","crazy","crying","ghost","glad","grin","happy","mug","quiet","scared","shock",
//			"shy","sleepy","smile","smirk","softly","surprise","tear","tired","vomiting"
//	};
//	public String[] name2 = {
//			"angry","awkward","bittersmile","blush","cool","crazy","crying","ghost","glad","grin","happy","mug","quiet","scared","shock",
//			"shy","sleepy","smile","smirk","softly","surprise","tear","tired","vomiting"
//	};
//	public int[] themeid2 = {
//			10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,
//			10,10,10,10,10,10,10,10,10
//	};
//	public String[] code = {"ali01",
//			"",
//			"","","","","","","","","","","","","","","","","","","","",
//			"","","","","","","","","","","","","","","","","","","","",
//			"","","","","","","","","","","","","","","","","","","","",
//			"","","","","","","","","","","","","","","","","","","","",
//			"","","","","","","","","","","","","","","","","","","","",
//			"","","","","","","","","","","","","","","","","","","","",
//			"","","","","","","","","","","","","","","","","","","","",
//			"","","","","","","","","","","","","","","","","","","","",
//			"","","","","","","","","","","","","","","","","","","","",
//			"","","","","","","","","","","","","","","","","","","","",
//			"","","","","","","","","","","","","","","","","","","","",
//			"","","","","","","","","","","","","","","","","","","","",
//			"","","","","","","","","","","","","","","","","","","","",
//			"","","","","","","","","","","","","","","","","","","","",
//			"","","","","","","","","","","","","","","","","","","","",
//			"","","","","","","","","","","","","","","","","","","","",
//			"","","","","","","","","","","","","","","","","","","","",
//			"","","","","","","","","","","","","","","","","","","","",
//			"","","","","","","","","","","","","","","","","","","","",
//			"","","","","","","","","","","","","","","","","","","","",
//			"","","","","","","","","","","","","","","","","","","","",
//			"","","","","","","","","","","","","","","","","","","","",
//			"","","","","","","","","","","","","","","","","","","","",
//			"","","","","","","","","","(吃饭)","(调皮)","(尴尬)","(汗)","(惊恐)","(囧)","(可爱)","(酷)","(流口水)","(色)","(生病)",
//			"(叹气)","(淘气)","(舔)","(偷笑)","(吐)","(吻)","(晕)","(住嘴)","(大笑)","(害羞)","(口罩)","(哭)","(困)","(难过)","(生气)","(书呆子)","(微笑)","(不)","(惊讶)","(kb)",
//			"(sx)","(gl)","(yl)","(hold1)","(cold)","(bw)","(nuomi)","(rs)","(sbq)","(ymy)","(th)","(mb)","(mc)","(sn)",
//	};
//	public String[] path = {"ali01",
//			"emoji_F09F9884","emoji_F09F988A","emoji_F09F9883","emoji_E298BA","emoji_F09F9889",
//			"emoji_F09F988D","emoji_F09F9898","emoji_F09F989A","emoji_F09F98B3","emoji_F09F988C",
//			"emoji_F09F9881","emoji_F09F989C","emoji_F09F989D","emoji_F09F9892","emoji_F09F988F",
//			"emoji_F09F9893","emoji_F09F9894","emoji_F09F989E","emoji_F09F9896","emoji_F09F98A5",
//			"emoji_F09F98B0","emoji_F09F98A8","emoji_F09F98A3","emoji_F09F98A2","emoji_F09F98AD",
//			"emoji_F09F9882","emoji_F09F98B2","emoji_F09F98B1","emoji_F09F98A0","emoji_F09F98A1",
//			"emoji_F09F98AA","emoji_F09F98B7","emoji_F09F91BF","emoji_F09F91BD","emoji_F09F929B",
//			"emoji_F09F9299","emoji_F09F929C","emoji_F09F9297","emoji_F09F929A","emoji_E29DA4",
//			"emoji_F09F9294","emoji_F09F9293","emoji_F09F9298","emoji_E29CA8","emoji_E2AD90",
//			"emoji_F09F92A2","emoji_E29D95","emoji_E29D94","emoji_F09F92A4","emoji_F09F92A8",
//			"emoji_F09F92A6","emoji_F09F8EB6","emoji_F09F8EB5","emoji_F09F94A5","emoji_F09F92A9",
//			"emoji_F09F918D","emoji_F09F918E","emoji_F09F918C","emoji_F09F918A","emoji_E29C8A",
//			"emoji_E29C8C","emoji_F09F918B","emoji_E29C8B","emoji_E29880","emoji_E29894",
//			"emoji_E29881","emoji_E29B84","emoji_F09F8C99","emoji_E29AA1","emoji_F09F8C80",
//			"emoji_F09F8C8A","emoji_F09F90B1","emoji_F09F90B6","emoji_F09F90AD","emoji_F09F90B9",
//			"emoji_F09F90B0","emoji_F09F90BA","emoji_F09F90B8","emoji_F09F90AF","emoji_F09F90A8",
//			"emoji_F09F90BB","emoji_F09F90B7","emoji_F09F90AE","emoji_F09F9097","emoji_F09F90B5",
//			"emoji_F09F9092","emoji_F09F90B4","emoji_F09F908E","emoji_F09F90AB","emoji_F09F9091",
//			"emoji_F09F9098","emoji_F09F908D","emoji_F09F90A6","emoji_F09F90A4","emoji_F09F9094",
//			"emoji_F09F90A7","emoji_F09F909B","emoji_F09F9099","emoji_F09F90A0","emoji_F09F909F",
//			"emoji_F09F90B3","emoji_F09F90AC","emoji_F09F9290","emoji_F09F8CB8","emoji_F09F8CB7",
//			"emoji_F09F8D80","emoji_F09F8CB9","emoji_F09F8CBB","emoji_F09F8CBA","emoji_F09F8D81",
//			"emoji_F09F8D83","emoji_F09F8D82","emoji_F09F8CB4","emoji_F09F8CB5","emoji_F09F8CBE",
//			"emoji_F09F909A","emoji_F09F8E8D","emoji_F09F929D","emoji_F09F8E8E","emoji_F09F8E92",
//			"emoji_F09F8E93","emoji_F09F8E8F","emoji_F09F8E86","emoji_F09F8E87","emoji_F09F8E90",
//			"emoji_F09F8E91","emoji_F09F8E83","emoji_F09F91BB","emoji_F09F8E85","emoji_F09F8E84",
//			"emoji_F09F8E81","emoji_F09F9494","emoji_F09F8E89","emoji_F09F8E88","emoji_F09F92BF",
//			"emoji_F09F9380","emoji_F09F93B7","emoji_F09F8EA5","emoji_F09F92BB","emoji_F09F93BA",
//			"emoji_F09F93B1","emoji_F09F93A0","emoji_E2988E","emoji_F09F92BD","emoji_F09F93BC",
//			"emoji_F09F948A","emoji_F09F93A2","emoji_F09F93A3","emoji_F09F93BB","emoji_F09F93A1",
//			"emoji_E29EBF","emoji_F09F948D","emoji_F09F9493","emoji_F09F9492","emoji_F09F9491",
//			"emoji_E29C82","emoji_F09F94A8","emoji_F09F92A1","emoji_F09F93B2","emoji_F09F93A9",
//			"emoji_F09F93AB","emoji_F09F93AE","emoji_F09F9B80","emoji_F09F9ABD","emoji_F09F92BA",
//			"emoji_F09F92B0","emoji_F09F94B1","emoji_F09F9AAC","emoji_F09F92A3","emoji_F09F94AB",
//			"emoji_F09F928A","emoji_F09F9289","emoji_F09F8F88","emoji_F09F8F80","emoji_E29ABD",
//			"emoji_E29ABE","emoji_F09F8EBE","emoji_E29BB3","emoji_F09F8EB1","emoji_F09F8FA0",
//			"emoji_F09F8FAB","emoji_F09F8FA2","emoji_F09F8FA3","emoji_F09F8FA5","emoji_F09F8FA6",
//			"emoji_F09F8FAA","emoji_F09F9292","emoji_F09F8FA9","emoji_F09F8FA8","emoji_E29BAA",
//			"emoji_F09F8FAC","emoji_F09F8C87","emoji_F09F8C86","emoji_F09F8FA7","emoji_F09F8FAF",
//			"emoji_F09F8FB0","emoji_E29BBA","emoji_F09F8FAD","emoji_F09F97BC","emoji_F09F97BB",
//			"emoji_F09F8C84","emoji_F09F8C85","emoji_F09F8C83","emoji_F09F97BD","emoji_F09F8C88",
//			"emoji_F09F8EA1","emoji_E29BB2","emoji_F09F8EA2","emoji_F09F9AA2","emoji_F09F9AA4",
//			"emoji_E29BB5","emoji_E29C88","emoji_F09F9A80","emoji_F09F9AB2","emoji_F09F9A99",
//			"emoji_F09F9A97","emoji_F09F9A95","emoji_F09F9A8C","emoji_F09F9A93","emoji_F09F9A92",
//			"emoji_F09F9A91","emoji_F09F9A9A","emoji_F09F9A83","emoji_F09F9A89","emoji_F09F9A84",
//			"emoji_F09F9A85","emoji_F09F8EAB","emoji_E29BBD","emoji_F09F9AA5","emoji_E29AA0",
//			"emoji_F09F9AA7","emoji_F09F94B0","emoji_F09F8EB0","emoji_F09F9A8F","emoji_F09F9288",
//			"emoji_E299A8","emoji_F09F8F81","emoji_F09F8E8C","emoji_F09F87AFF09F87B5",
//			"emoji_F09F87B0F09F87B7","emoji_F09F87A8F09F87B3","emoji_F09F87BAF09F87B8",
//			"emoji_31E283A3","emoji_32E283A3","emoji_33E283A3","emoji_34E283A3","emoji_35E283A3",
//			"emoji_36E283A3","emoji_37E283A3","emoji_38E283A3","emoji_39E283A3","emoji_30E283A3",
//			"emoji_23E283A3","emoji_E2AC86","emoji_E2AC87","emoji_E2AC85","emoji_E29EA1","emoji_E28697","emoji_E28696",
//			"emoji_E28698","emoji_E28699","emoji_E29780","emoji_E296B6","emoji_E28FAA","emoji_E28FA9","emoji_F09F8697",
//			"emoji_F09F8695","emoji_F09F949D","emoji_F09F8699","emoji_F09F8692","emoji_F09F8EA6","emoji_F09F8881",
//			"emoji_F09F93B6","emoji_F09F88B5","emoji_F09F88B3","emoji_F09F8990","emoji_F09F88B9","emoji_F09F88AF",
//			"emoji_F09F88BA","emoji_F09F88B6","emoji_F09F889A","emoji_F09F88B7","emoji_F09F88B8","emoji_F09F8882",
//			"emoji_F09F9ABB","emoji_F09F9AB9","emoji_F09F9ABA","emoji_F09F9ABC","emoji_F09F9AAD","emoji_F09F85BF",
//			"emoji_E299BF","emoji_F09F9A87","emoji_F09F9ABE","emoji_E38A99","emoji_E38A97","emoji_F09F949E",
//			"emoji_F09F8694","emoji_E29CB3","emoji_E29CB4","emoji_F09F929F","emoji_F09F869A","emoji_F09F93B3",
//			"emoji_F09F93B4","emoji_F09F92B9","emoji_F09F92B1","emoji_F09F928B","emoji_F09F928D","emoji_F09F928E",
//			"emoji_F09F8E80","emoji_F09F928F","emoji_F09F9291","emoji_E2AD95","emoji_E29D8C","emoji_E29D93",
//			"emoji_E29D97","emoji_E2989D","emoji_F09F92AA","emoji_F09F918F","emoji_F09F998F","emoji_F09F9180",
//			"emoji_F09F9182","emoji_F09F9183","emoji_F09F91A6","emoji_F09F91A7","emoji_F09F91A8","emoji_F09F91A9",
//			"emoji_F09F91B4","emoji_F09F91B5","emoji_F09F91B6","emoji_F09F9286","emoji_F09F9287","emoji_F09F9985",
//			"emoji_F09F9986","emoji_F09F9987","emoji_F09F8D8E","emoji_F09F8D89","emoji_F09F8D93","emoji_E29895",
//			"emoji_F09F8D9A","emoji_F09F8DA7","emoji_F09F8DB0","emoji_F09F8DBB","emoji_F09F8EA8","emoji_F09F8F8A",
//			"emoji_F09F8F83","emoji_F09F8F84","emoji_F09F91A0","emoji_F09F9191","emoji_F09F9396","emoji_E29991",
//			"emoji_F09F9592","emoji_F09F8DB8","emoji_E29992","emoji_F09F9593","emoji_F09F8DB3","emoji_F09F9594",
//			"emoji_E284A2","emoji_F09F9595","emoji_F09F8DBA","emoji_F09F91B7","emoji_F09F87ABF09F87B7","emoji_F09F91B8",
//			"emoji_F09F87A9F09F87AA","emoji_F09F8EA7","emoji_F09F87AEF09F87B9","emoji_F09F9282","emoji_F09F8DB6",
//			"emoji_F09F9184","emoji_F09F91A1","emoji_F09F9283","emoji_F09F91A2","emoji_F09F9284","emoji_F09F8C82",
//			"emoji_F09F94B2","emoji_F09F8DA6","emoji_F09F9285","emoji_E299A5","emoji_F09F94B3","emoji_F09F8D9F",
//			"emoji_F09F8D86","emoji_E299A6","emoji_F09F8DA1","emoji_F09F8E82","emoji_E299A0","emoji_F09F8D98",
//			"emoji_F09F8DB1","emoji_E29993","emoji_E299A3","emoji_F09F9280","emoji_F09F8DB2","emoji_F09F91BE",
//			"emoji_E29B8E","emoji_F09F8D9D","emoji_F09F9186","emoji_E380BD","emoji_F09F9596","emoji_F09F92BC",
//			"emoji_F09F9187","emoji_F09F8084","emoji_F09F94AF","emoji_F09F9597","emoji_E29988","emoji_F09F9598",
//			"emoji_C2A9","emoji_F09F87ACF09F87A7","emoji_F09F9599","emoji_C2AE","emoji_F09F8EA4","emoji_F09F87AAF09F87B8",
//			"emoji_F09F959A","emoji_F09F8EA9","emoji_F09F87B7F09F87BA","emoji_F09F959B","emoji_F09F91BC","emoji_F09F939D",
//			"emoji_F09F85B0","emoji_F09F91B1","emoji_F09F9194","emoji_F09F9190","emoji_F09F85B1","emoji_F09F91B2","emoji_F09F9AB6",
//			"emoji_F09F868E","emoji_F09F9198","emoji_F09F91B3","emoji_F09F85BE","emoji_F09F9199","emoji_F09F8D9C","emoji_F09F91A3",
//			"emoji_F09F919C","emoji_F09F9188","emoji_F09F8D9B","emoji_F09F8EAC","emoji_F09F9189","emoji_F09F8D99","emoji_E29989",
//			"emoji_F09F998C","emoji_F09F8D94","emoji_E2998A","emoji_F09F91AB","emoji_F09F8DA2","emoji_E2998B","emoji_F09F9192",
//			"emoji_F09F91AF","emoji_F09F8DA3","emoji_E2998C","emoji_F09F9197","emoji_F09F8EBF","emoji_F09F8D8A","emoji_E2998D",
//			"emoji_F09F8EAF","emoji_F09F9281","emoji_F09F8EB7","emoji_E2998E","emoji_F09F8F86","emoji_F09F94B4","emoji_F09F8C9F",
//			"emoji_F09F8EB8","emoji_F09F8D9E","emoji_F09F91AE","emoji_E2998F","emoji_F09F9590","emoji_F09F9195","emoji_F09F8EBA",
//			"emoji_F09F919F","emoji_F09F8D85","emoji_E29990","emoji_F09F9591","emoji_F09F8DB5","emoji_F09F8DB4",
//			"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16",
//			"17","18","19","20","21","22","23","24","25","26","27","28","29","30","31",
//			"32","33","34","35","36","37","38","39","40","41","42","43","44","45"
//	};
//	public String[] theme={
//			"1","1","1","1","1","1","1","1","1","1","1","1","1","1","1","1","1","1","1",
//			"1","1","1","1","1","1","1","1","1","1","1","1","1","1","1","1","1","1","1",
//			"1","1","1","1","1","1","1","1"	
//	};
//	public String[] name={"ali01",
//			"e415","e056","e057","e414","e405","e106","e418","e417","e40d","e40a",
//			"e404","e105","e409","e40e","e402","e108","e403","e058","e407","e401",
//			"e40f","e40b","e406","e413","e411","e412","e410","e107","e059","e416",
//			"e408","e40c","e11a","e10c","e32c","e32a","e32d","e328","e32b","e022",
//			"e023","e327","e329","e32e","e32f","e334","e337","e336","e13c","e330",
//			"e331","e326","e03e","e11d","e05a","e00e","e421","e420","e00d","e010",
//			"e011","e41e","e012","e04a","e04b","e049","e048","e04c","e13d","e443",
//			"e43e","e04f","e052","e053","e524","e52c","e52a","e531","e050","e527",
//			"e051","e10b","e52b","e52f","e109","e528","e01a","e134","e530","e529",
//			"e526","e52d","e521","e523","e52e","e055","e525","e10a","e522","e019",
//			"e054","e520","e306","e030","e304","e110","e032","e305","e303","e118",
//			"e447","e119","e307","e308","e444","e441","e436","e437","e438","e43a",
//			"e439","e43b","e117","e440","e442","e446","e445","e11b","e448","e033",
//			"e112","e325","e312","e310","e126","e127","e008","e03d","e00c","e12a",
//			"e00a","e00b","e009","e316","e129","e141","e142","e317","e128","e14b",
//			"e211","e114","e145","e144","e03f","e313","e116","e10f","e104","e103",
//			"e101","e102","e13f","e140","e11f","e12f","e031","e30e","e311","e113",
//			"e30f","e13b","e42b","e42a","e018","e016","e015","e014","e42c","e036",
//			"e157","e038","e153","e155","e14d","e156","e43d","e501","e158","e037",
//			"e504","e44a","e146","e154","e505","e506","e122","e508","e509","e03b",
//			"e04d","e449","e44b","e51d","e44c","e124","e121","e433","e202","e135",
//			"e01c","e01d","e10d","e136","e42e","e01b","e15a","e159","e432","e430",
//			"e431","e42f","e01e","e039","e435","e01f","e125","e03a","e14e","e252",
//			"e137","e209","e133","e150","e320","e123","e132","e143","e50b","e514",
//			"e513","e50c","e21c","e21d","e21e","e21f","e220","e221","e222","e223",
//			"e224","e225","e210","e232","e233","e235","e234","e236","e237","e238",
//			"e239","e23b","e23a","e23d","e23c","e24d","e212","e24c","e213","e214",
//			"e507","e203","e20b","e22a","e22b","e226","e227","e22c","e22d","e215",
//			"e216","e217","e218","e228","e151","e138","e139","e13a","e208","e14f",
//			"e20a","e434","e309","e315","e30d","e207","e229","e206","e205","e204",
//			"e12e","e250","e251","e14a","e149","e003","e034","e035","e314","e111",
//			"e425","e332","e333","e020","e021","e00f","e14c","e41f","e41d","e419",
//			"e41b","e41a","e001","e002","e004","e005","e518","e519","e51a","e31e",
//			"e31f","e423","e424","e426","e345","e348","e347","e045","e33e","e43f",
//			"e046","e30c","e502","e42d","e115","e017","e13e","e10e","e148","e248",
//			"e026","e044","e249","e027","e147","e028","e537","e029","e047","e51b",
//			"e50d","e51c","e50e","e30a","e50f","e51e","e30b","e41c","e31a","e51f",
//			"e31b","e31c","e43c","e21a","e33a","e31d","e20c","e21b","e33b","e34a",
//			"e20d","e33c","e34b","e20e","e33d","e34c","e24a","e20f","e11c","e34d",
//			"e12b","e24b","e33f","e22e","e12c","e02a","e11e","e22f","e12d","e23e",
//			"e02b","e23f","e02c","e24e","e510","e02d","e24f","e03c","e511","e02e",
//			"e503","e512","e02f","e04e","e301","e532","e515","e302","e422","e533",
//			"e516","e201","e534","e321","e517","e535","e322","e340","e536","e323",
//			"e230","e341","e324","e231","e342","e240","e427","e120","e241","e428",
//			"e343","e242","e318","e429","e344","e243","e319","e013","e346","e244",
//			"e130","e253","e040","e245","e131","e219","e335","e041","e339","e152",
//			"e246","e024","e006","e042","e007","e349","e247","e025","e338","e043",
//			"(吃饭)","(调皮)","(尴尬)","(汗)","(惊恐)","(囧)","(可爱)","(酷)","(流口水)",
//			"(色)","(生病)","(叹气)","(淘气)","(舔)","(偷笑)","(吐)","(吻)","(晕)","(住嘴)","(大笑)",
//			"(害羞)","(口罩)","(哭)","(困)","(难过)","(生气)","(书呆子)","(微笑)","(不)","(惊讶)","(kb)",
//			"(sx)","(gl)","(yl)","(hold1)","(cold)","(bw)","(nuomi)","(rs)","(sbq)","(ymy)",
//			"(th)","(mb)","(mc)","(sn)"
//	};
//	public String[] codetmp = {
//			"e415","e056","e057","e414","e405","e106","e418","e417","e40d","e40a",
//			"e404","e105","e409","e40e","e402","e108","e403","e058","e407","e401",
//			"e40f","e40b","e406","e413","e411","e412","e410","e107","e059","e416",
//			"e408","e40c","e11a","e10c","e32c","e32a","e32d","e328","e32b","e022",
//			"e023","e327","e329","e32e","e32f","e334","e337","e336","e13c","e330",
//			"e331","e326","e03e","e11d","e05a","e00e","e421","e420","e00d","e010",
//			"e011","e41e","e012","e04a","e04b","e049","e048","e04c","e13d","e443",
//			"e43e","e04f","e052","e053","e524","e52c","e52a","e531","e050","e527",
//			"e051","e10b","e52b","e52f","e109","e528","e01a","e134","e530","e529",
//			"e526","e52d","e521","e523","e52e","e055","e525","e10a","e522","e019",
//			"e054","e520","e306","e030","e304","e110","e032","e305","e303","e118",
//			"e447","e119","e307","e308","e444","e441","e436","e437","e438","e43a",
//			"e439","e43b","e117","e440","e442","e446","e445","e11b","e448","e033",
//			"e112","e325","e312","e310","e126","e127","e008","e03d","e00c","e12a",
//			"e00a","e00b","e009","e316","e129","e141","e142","e317","e128","e14b",
//			"e211","e114","e145","e144","e03f","e313","e116","e10f","e104","e103",
//			"e101","e102","e13f","e140","e11f","e12f","e031","e30e","e311","e113",
//			"e30f","e13b","e42b","e42a","e018","e016","e015","e014","e42c","e036",
//			"e157","e038","e153","e155","e14d","e156","e43d","e501","e158","e037",
//			"e504","e44a","e146","e154","e505","e506","e122","e508","e509","e03b",
//			"e04d","e449","e44b","e51d","e44c","e124","e121","e433","e202","e135",
//			"e01c","e01d","e10d","e136","e42e","e01b","e15a","e159","e432","e430",
//			"e431","e42f","e01e","e039","e435","e01f","e125","e03a","e14e","e252",
//			"e137","e209","e133","e150","e320","e123","e132","e143","e50b","e514",
//			"e513","e50c","e21c","e21d","e21e","e21f","e220","e221","e222","e223",
//			"e224","e225","e210","e232","e233","e235","e234","e236","e237","e238",
//			"e239","e23b","e23a","e23d","e23c","e24d","e212","e24c","e213","e214",
//			"e507","e203","e20b","e22a","e22b","e226","e227","e22c","e22d","e215",
//			"e216","e217","e218","e228","e151","e138","e139","e13a","e208","e14f",
//			"e20a","e434","e309","e315","e30d","e207","e229","e206","e205","e204",
//			"e12e","e250","e251","e14a","e149","e003","e034","e035","e314","e111",
//			"e425","e332","e333","e020","e021","e00f","e14c","e41f","e41d","e419",
//			"e41b","e41a","e001","e002","e004","e005","e518","e519","e51a","e31e",
//			"e31f","e423","e424","e426","e345","e348","e347","e045","e33e","e43f",
//			"e046","e30c","e502","e42d","e115","e017","e13e","e10e","e148","e248",
//			"e026","e044","e249","e027","e147","e028","e537","e029","e047","e51b",
//			"e50d","e51c","e50e","e30a","e50f","e51e","e30b","e41c","e31a","e51f",
//			"e31b","e31c","e43c","e21a","e33a","e31d","e20c","e21b","e33b","e34a",
//			"e20d","e33c","e34b","e20e","e33d","e34c","e24a","e20f","e11c","e34d",
//			"e12b","e24b","e33f","e22e","e12c","e02a","e11e","e22f","e12d","e23e",
//			"e02b","e23f","e02c","e24e","e510","e02d","e24f","e03c","e511","e02e",
//			"e503","e512","e02f","e04e","e301","e532","e515","e302","e422","e533",
//			"e516","e201","e534","e321","e517","e535","e322","e340","e536","e323",
//			"e230","e341","e324","e231","e342","e240","e427","e120","e241","e428",
//			"e343","e242","e318","e429","e344","e243","e319","e013","e346","e244",
//			"e130","e253","e040","e245","e131","e219","e335","e041","e339","e152",
//			"e246","e024","e006","e042","e007","e349","e247","e025","e338","e043"
//	};
//	public String[] codetmp2={"(吃饭)","(调皮)","(尴尬)","(汗)","(惊恐)",
//			"(囧)","(可爱)","(酷)","(流口水)","(色)","(生病)","(叹气)","(淘气)","(舔)","(偷笑)","(吐)","(吻)","(晕)","(住嘴)",
//			"(大笑)","(害羞)","(口罩)","(哭)","(困)","(难过)","(生气)","(书呆子)","(微笑)","(不)","(惊讶)","(kb)","(sx)","(gl)",
//			"(yl)","(hold1)","(cold)","(bw)","(nuomi)","(rs)","(sbq)","(ymy)","(th)","(mb)","(mc)","(sn)"};
	
//	public int size(){
//		return code1.length;
//	}
	public void logsize(){
		ErrLog.ll("code size "+code.length+ " path size " + path.length+"  name size " + name.length+" theme size " + themeid.length );
	}
	
}	
