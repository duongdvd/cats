package jp.co.willwave.aca;

import jp.co.willwave.aca.model.constant.Constant;
import jp.co.willwave.aca.model.entity.UsersEntity;
import jp.co.willwave.aca.utilities.ConversionUtil;
import jp.co.willwave.aca.utilities.PasswordUtils;
import org.junit.Test;

import java.lang.reflect.Method;

public class TestJUnit {

    @Test
    public void GeneratePasswordDevices() {
        String password = "1";
        String salt = Constant.SHA_512_PASSWORD_KEY;
        String passwordEncode = PasswordUtils.generateSecurePassword(password, salt);
        System.out.println("Password " + password);
        System.out.println("Salt " + Constant.SHA_512_PASSWORD_KEY);
        System.out.println("passwordEncode " + passwordEncode);
    }

    @Test
    public void splitString() {
        String resources = "D_1_2_3_4_5";
        String[] chars = resources.split("_");
        for (int i = 0; i < chars.length; i++) {
            System.out.println(chars[i] + " ");
        }
    }

    @Test
    public void test() throws ClassNotFoundException {
        Class division = Class.forName("jp.co.willwave.aca.model.entity.DivisionsEntity");
        Class devices = Class.forName("jp.co.willwave.aca.model.entity.DevicesEntity");
        Method[] methodDi = division.getMethods();
        for (int i = 0; i < methodDi.length; i++) {
            System.out.println("method division " + methodDi[i].getName());
        }
        Method[] methodDe = devices.getMethods();
        for (int i = 0; i < methodDi.length; i++) {
            System.out.println("method devi " + methodDe[i].getName());
        }
    }

    @Test
    public void testConvert() {
        UsersEntity usersEntity = new UsersEntity();
        usersEntity.setId(1L);
        usersEntity.setFirstName("AnhvvF");

        UsersEntity usersEntity2 = new UsersEntity();
        usersEntity2.setId(1L);
        usersEntity2.setLastName("AnhvvL");
        usersEntity2.setFirstName("AnhvvFO");

        ConversionUtil.mapper(usersEntity, usersEntity2);
        System.out.println("id " + usersEntity2.getId());
        System.out.println("first name " + usersEntity2.getFirstName());
        System.out.println("last name " + usersEntity2.getLastName());
    }
}
