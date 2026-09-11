package ru.aston.user.service.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aston.user.service.config.HibernateUtil;
import ru.aston.user.service.entity.User;
import ru.aston.user.service.exception.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserDaoImpl implements UserDao {

    private static final Logger logger = LoggerFactory.getLogger(UserDaoImpl.class);

    @Override
    public User save(User user) {
        Transaction transaction = null;
        logger.debug("Получен пользователь: id = {}, name = {}", user.getId(), user.getName());
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            logger.debug("Сессия Hibernate открыта");
            transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
            logger.info("Пользователь {} записан в базу данных", user.getId());
            return user;
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
                logger.debug("Выполнен откат транзакции");
            }
            logger.error("Ошибка при сохранении информации о пользователе с id={}", user.getId());
            throw new UserNotSaveException("Failed to save user");
        }
    }

    @Override
    public User update(User user) {
        Transaction transaction = null;
        logger.debug("Получен пользователь: id = {}, name = {}", user.getId(), user.getName());
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            logger.debug("Сессия Hibernate открыта");
            transaction = session.beginTransaction();
            User updatedUser = session.merge(user);
            transaction.commit();
            logger.info("Информация о пользователе {} обновлена", user.getId());
            return updatedUser;
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
                logger.debug("Выполнен откат транзакции");
            }
            logger.error("Ошибка при обновлении информации о пользователе с id={}", user.getId());
            throw new UserNotUpdateException(user.getId());
        }
    }

    @Override
    public void delete(Long id) {
        Transaction transaction = null;
        logger.debug("Получен id = {}", id);
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            logger.debug("Сессия Hibernate открыта");
            transaction = session.beginTransaction();
            User user = session.get(User.class, id);
            if (user != null) {
                logger.debug("Пользователь с id = {} найдена", id);
                session.remove(user);
                logger.info("Пользователь с id = {} удален", id);
            } else {
                logger.warn("Пользователь с id {} не найден в БД", id);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
                logger.debug("Выполнен откат транзакции");
            }
            logger.error("Ошибка при удалении пользователя с id={}", id);
            throw new UserNotDeleteException(id);
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            logger.debug("Сессия Hibernate открыта");
            User user = session.get(User.class, id);
            if (user != null) {
                logger.info("Пользователь найден: id={}, name={}, email={}",
                        user.getId(), user.getName(), user.getEmail());
                if (logger.isDebugEnabled()) {
                    logger.debug("Полная информация о пользователе: {}", user);
                }

                return Optional.of(user);
            } else {
                logger.debug("Пользователь с id {} не найден", id);
                return Optional.empty();
            }
        } catch (Exception e) {
            logger.info("Ошибка при поиске пользователя по id = {}", id);
            throw new UserNotFoundException(id);
        }
    }

    @Override
    public List<User> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            logger.debug("Сессия Hibernate открыта");
            Query<User> query = session.createQuery("FROM User ORDER BY id", User.class);
            List<User> users = query.list();
            int count = users != null ? users.size() : 0;
            if (count > 0) {
                logger.info("Найдено {} пользователей", count);
                if (logger.isDebugEnabled() && count > 0) {
                    String sample = users.stream()
                            .limit(5)
                            .map(u -> String.format("%d:%s", u.getId(), u.getName()))
                            .collect(Collectors.joining(", "));
                    logger.debug("Первые 5 пользователей: {}", sample);
                    if (count > 5) {
                        logger.debug("... и еще {} пользователей", count - 5);
                    }
                }
            } else {
                logger.debug("Пользователи не найдены");
            }

            return users;
        } catch (Exception e) {
            logger.error("Ошибка при получении всех пользователей", e);
            throw new UserNotFoundException("Failed to find users");
        }
    }

    @Override
    public boolean existByEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            logger.debug("Сессия Hibernate открыта для проверки email: {}", email);
            Query<Long> query = session.createQuery(
                    "SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class);
            query.setParameter("email", email);
            Long count = query.uniqueResult();
            logger.debug("Выполнение запроса COUNT для email: {}", email);
            boolean exists = count != null && count > 0;
            if (exists) {
                logger.info("Пользователь с email {} существует (найдено записей: {})", email, count);
            } else {
                logger.debug("Пользователь с email {} не найден", email);
            }
            return exists;
        } catch (Exception e) {
            logger.error("Ошибка при проверке существования email {} ", email);
            throw new EmailCheckException("Failed to check email existence");
        }
    }
}
