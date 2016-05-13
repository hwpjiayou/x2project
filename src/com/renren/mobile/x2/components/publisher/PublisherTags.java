package com.renren.mobile.x2.components.publisher;

import java.util.ArrayList;

import android.view.View;
import android.widget.ImageView;

import com.renren.mobile.x2.R;

public class PublisherTags {
	static final private String[] names = { "求组队", "求认识", "吐槽", "拜考神", "当学霸", "无聊ing", "末日后", "我饿了", "求帮忙", "二手" };
	static final private String[] descs = { " 找人一起组队玩吧～Dota，魔兽，三国杀，运动，聚会，什么都行～", " 求闺蜜求兄弟？爆下你的资料咯", "你点开了这里，就请自由地@#*$%#@%$#&*$%#@%$#&*", "考神：今天你拜我了么？", "驰骋考场数年，未曾一挂",
			"无聊的时候，就聊点什么呗", "告诉全世界，我活过了2012", "人是铁，饭是钢，一顿不吃饿得慌~", "遇到什么困难了？快告诉周围的朋友们吧，或许那个ta可以帮到你喔！", "想买还是想卖，吆喝一下吧！", };
	static final private int[] ids = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
	static final private int[] iconResourceIds = { R.drawable.publisher_tag_selector1, R.drawable.publisher_tag_selector2, R.drawable.publisher_tag_selector3,
			R.drawable.publisher_tag_selector4, R.drawable.publisher_tag_selector5, R.drawable.publisher_tag_selector6, R.drawable.publisher_tag_selector7,
			R.drawable.publisher_tag_selector8, R.drawable.publisher_tag_selector9, R.drawable.publisher_tag_selector10 };
	static final private int[] iconUnpressResourceIds = { R.drawable.publisher_tag_unpressed1, R.drawable.publisher_tag_unpressed2, R.drawable.publisher_tag_unpressed3,
		R.drawable.publisher_tag_unpressed4, R.drawable.publisher_tag_unpressed5, R.drawable.publisher_tag_unpressed6, R.drawable.publisher_tag_unpressed7,
		R.drawable.publisher_tag_unpressed8, R.drawable.publisher_tag_unpressed9, R.drawable.publisher_tag_unpressed10 };
	public static ArrayList<Tag> getTagList() {
		ArrayList<Tag> result = new ArrayList<PublisherTags.Tag>();
		for (int i = 0; i < names.length ; i++) {
			Tag tag = new Tag(names[i], descs[i], ids[i], iconResourceIds[i],iconUnpressResourceIds[i]);
			result.add(tag);
		}
		return result;
	}

	public static class Tag {
		public final String name;
		public final String desc;
		public final int id;
		public final int iconResourceId;
		public final int iconUnpressResourceId;
		public ImageView selectbg;
		public Tag(String name, String desc, int id, int iconResourceId,int iconUnpressResourceId) {
			this.name = name;
			this.desc = desc;
			this.id = id;
			this.iconResourceId = iconResourceId;
			this.iconUnpressResourceId = iconUnpressResourceId;
		}
		public void setImageView(ImageView v){
			this.selectbg = v;
		}
		public void setSelected(){
			if(selectbg!=null){
				selectbg.setVisibility(View.VISIBLE);
			}
		}
		public void setUnSelected(){
			if(selectbg!=null){
				selectbg.setVisibility(View.INVISIBLE);
			}
		}
	}
}
