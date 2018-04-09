
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

	private static SessionFactory sessionFactory;

	public static SessionFactory getSessionFactory() {

		if (sessionFactory == null) {

			sessionFactory = build();

		}
		return sessionFactory;

	}

	private static SessionFactory build() {

		try {

			return new Configuration().configure().buildSessionFactory();

		} catch (Throwable ex) {

			System.err.println("Initial SessionFactory creation failed: " + ex);
			throw new ExceptionInInitializerError(ex);
		}

	}

}
