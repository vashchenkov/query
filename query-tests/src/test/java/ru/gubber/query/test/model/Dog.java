package ru.gubber.query.test.model;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by gubber on 29.09.2015.
 */
@Entity
@Table(name = "DOGS")
public class Dog {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID", nullable = false)
	private long id;

	@Column(name = "NICK_NAME")
	private String nickname;

	@ManyToOne(targetEntity = Dog.class, fetch = FetchType.EAGER)
	@JoinColumn(name = "PARENT_ID")
	private Dog parent;

	@Column(name = "BIRTH_DATE")
	private Date bithDate;

	@Column(name = "ALIVE", nullable = false)
	private boolean alive;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public Dog getParent() {
		return parent;
	}

	public void setParent(Dog parent) {
		this.parent = parent;
	}

	public Date getBithDate() {
		return bithDate;
	}

	public void setBithDate(Date bithDate) {
		this.bithDate = bithDate;
	}

	public boolean isAlive() {
		return alive;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}
}