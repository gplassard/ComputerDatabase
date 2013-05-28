package com.excilys.computerdatabase.dao;

import com.excilys.computerdatabase.model.Company;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: gplassard
 * Date: 24/05/13
 * Time: 12:20
 * To change this template use File | Settings | File Templates.
 */
public enum JdbcCompanyDao implements CompanyDao{
    INSTANCE;
    private static final String FIND_BY_ID = "SELECT * FROM company WHERE id=?";
    private static final String FIND_BY_NAME = "SELECT * FROM company WHERE name=?";
    private static final String SAVE = "INSERT INTO company(name) VALUES(?)";
    private static final String UPDATE = "UPDATE company SET name=? WHERE id=?";
    private static final String GET_ALL = "SELECT * FROM company ORDER BY name";
    private static final String DELETE = "DELETE FROM company";

    private Company companyFromTuple(ResultSet resultSet) throws SQLException {
        Company company = new Company();
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        company.setId(id);
        company.setName(name);
        return company;
    }

    @Override
    public Company findById(int companyId) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = JdbcUtils.getConnection();
            statement = connection.prepareStatement(FIND_BY_ID);
            statement.setInt(1,companyId);
            resultSet = statement.executeQuery();
            if (resultSet.next()){
                return companyFromTuple(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            JdbcUtils.closeResultSet(resultSet);
            JdbcUtils.closeStatement(statement);
            JdbcUtils.closeConnection(connection);
        }
        return null;
    }

    @Override
    public Company findByName(String companyName) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = JdbcUtils.getConnection();
            statement = connection.prepareStatement(FIND_BY_NAME);
            statement.setString(1, companyName);
            resultSet = statement.executeQuery();
            if (resultSet.next()){
                return companyFromTuple(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }finally {
            JdbcUtils.closeResultSet(resultSet);
            JdbcUtils.closeStatement(statement);
            JdbcUtils.closeConnection(connection);
        }
        return null;
    }

    private void save(Company company) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultKey = null;
        try {
            connection = JdbcUtils.getConnection();
            statement =  connection.prepareStatement(SAVE, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, company.getName());
            statement.execute();
            resultKey = statement.getGeneratedKeys();
            resultKey.next();
            int id = resultKey.getInt(1);
            company.setId(id);
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }finally {
            JdbcUtils.closeResultSet(resultKey);
            JdbcUtils.closeStatement(statement);
            JdbcUtils.closeConnection(connection);
        }
    }

    private void update(Company company) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = JdbcUtils.getConnection();
            statement =  connection.prepareStatement(UPDATE);
            statement.setString(1, company.getName());
            statement.setInt(2,company.getId());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }finally {
            JdbcUtils.closeStatement(statement);
            JdbcUtils.closeConnection(connection);
        }
    }

    @Override
    public List<Company> getAll() {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<Company> result = new ArrayList<Company>();
        try {
            connection = JdbcUtils.getConnection();
            statement = connection.prepareStatement(GET_ALL);
            resultSet = statement.executeQuery();
            while (resultSet.next()){
                result.add(companyFromTuple(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }finally {
            JdbcUtils.closeResultSet(resultSet);
            JdbcUtils.closeStatement(statement);
            JdbcUtils.closeConnection(connection);
        }
        return result;
    }


    @Override
    public void saveOrUpdate(Company company) {
        if (findById(company.getId()) == null){
            save(company);
        }
        else{
            update(company);
        }
    }

    @Override
    public void deleteAll() {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = JdbcUtils.getConnection();
            statement =  connection.prepareStatement(DELETE);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            JdbcUtils.closeStatement(statement);
            JdbcUtils.closeConnection(connection);
        }
    }
}
