package testy.gubber.query.model;

import jakarta.persistence.*;
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
	@JoinColumn(name = "MOTHER_ID")
	private Dog mother;

	@ManyToOne(targetEntity = Dog.class, fetch = FetchType.EAGER)
	@JoinColumn(name = "FATHER_ID")
	private Dog father;

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

	public Dog getMother() {
		return mother;
	}

	public void setMother(Dog parent) {
		this.mother = parent;
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

	public Dog getFather() {
		return father;
	}

	public void setFather(Dog father) {
		this.father = father;
	}
}