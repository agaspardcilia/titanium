package screach.titanium.gui.org.views;

import screach.titanium.core.wsp.Member;

public class MemberView {
	private Member member;
	
	public MemberView(int id, String username, boolean isYou) {
		member = new Member(id, username, isYou);
	}
	
	public MemberView(Member member) {
		this.member = member;
	}
	
	public int getId() {
		return member.getId();
	}
	
	public String getUsername() {
		return member.getUsername() + ((member.isYou()) ? "(you)" : "");
	}
	
	public Member getMember() {
		return member;
	}
	
	public boolean isYou() {
		return member.isYou();
	}
}
