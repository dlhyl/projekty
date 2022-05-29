import BaseSeeder from '@ioc:Adonis/Lucid/Seeder';
import User from 'App/Models/User';

export default class UserSeeder extends BaseSeeder {
  public async run() {
    await User.createMany([
      {
        firstname: 'Jozef',
        lastname: 'Bugal',
        username: 'jozkobugal',
        email: 'jozkobugal@gmail.com',
        image:
          'https://cdn.pixabay.com/photo/2014/07/09/10/04/man-388104_960_720.jpg',
        password: '123456',
      },
      {
        firstname: 'Nina',
        lastname: 'Hutson',
        username: 'ninahuts33',
        email: 'ninahutson@gmail.com',
        image:
          'https://cdn.pixabay.com/photo/2015/11/20/17/29/person-1053543_960_720.jpg',
        password: '123456',
      },
      {
        firstname: 'Erica',
        lastname: 'Rogers',
        username: 'rogereric9',
        email: 'rogereric9@gmail.com',
        image:
          'https://cdn.pixabay.com/photo/2015/08/03/22/51/person-874058_960_720.jpg',
        password: '123456',
      },
      {
        firstname: 'Thomas',
        lastname: 'Montgomery',
        username: 'montecarlo1231',
        email: 'montecarlo1231@gmail.com',
        image:
          'https://www.diethelmtravel.com/wp-content/uploads/2016/04/bill-gates-wealthiest-person.jpg',
        password: '123456',
      },
    ]);
  }
}
