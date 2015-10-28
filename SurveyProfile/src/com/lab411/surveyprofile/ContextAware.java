package com.lab411.surveyprofile;

public class ContextAware {
	int id;
	String name;
	String age;
	String sex;
	String game;
	String music;
	String image;
	String chat;
	String network;
	String time_call;

	public ContextAware() {
	}

	public ContextAware(String name, String age, String sex, String game,
			String music, String image, String chat, String network,
			String time_call) {
		this.name = name;
		this.age = age;
		this.sex = sex;
		this.game = game;
		this.music = music;
		this.image = image;
		this.chat = chat;
		this.network = network;
		this.time_call = time_call;
	}

	public ContextAware(int id, String name, String age, String sex, String game,
			String music, String image, String chat, String network,
			String time_call) {
		this.id = id;
		this.name = name;
		this.age = age;
		this.sex = sex;
		this.game = game;
		this.music = music;
		this.image = image;
		this.chat = chat;
		this.network = network;
		this.time_call = time_call;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getGame() {
		return game;
	}

	public void setGame(String game) {
		this.game = game;
	}

	public String getMusic() {
		return music;
	}

	public void setMusic(String music) {
		this.music = music;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getChat() {
		return chat;
	}

	public void setChat(String chat) {
		this.chat = chat;
	}

	public String getNetwork() {
		return network;
	}

	public void setNetwork(String network) {
		this.network = network;
	}

	public String getTime_call() {
		return time_call;
	}

	public void setTime_call(String time_call) {
		this.time_call = time_call;
	}
}
