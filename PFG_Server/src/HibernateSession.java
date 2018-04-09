
import org.hibernate.Session;

public class HibernateSession {

	public static Session session;

	public static void getTransaction() {
		session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
	}

	public static void commitclose() {

		session.getTransaction().commit();
		session.close();

	}
	
	public static void rollbackclose() {

		session.getTransaction().rollback();
		session.close();

	}
	
	
	
	
}
