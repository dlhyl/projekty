import type { HttpContextContract } from '@ioc:Adonis/Core/HttpContext';
import User from 'App/Models/User';
import RegisterUserValidator from 'App/Validators/RegisterUserValidator';

export default class AuthController {
  public async register({ request }: HttpContextContract) {
    const data = await request.validate(RegisterUserValidator);
    const user = await User.create(data);
    return user;
  }

  public async login({ auth, request, response }: HttpContextContract) {
    const email = request.input('email');
    const password = request.input('password');

    return auth
      .use('api')
      .attempt(email, password)
      .then((data) => {
        response.json(data);
      })
      .catch(() => {
        response.badRequest({
          errors: {
            email: 'Wrong e-mail or password',
            password: 'Wrong e-mail or password',
          },
        });
      });
  }

  public async logout({ auth }: HttpContextContract) {
    return auth.use('api').logout();
  }

  public async me({ auth }: HttpContextContract) {
    await auth.user!.load('privateChannels', (channelQuery) =>
      channelQuery.withCount('blacklist', (blacklistQuery) =>
        blacklistQuery.where('blacklistedId', auth.user!.id).as('kickCount')
      )
    );
    await auth.user!.load('publicChannels', (channelQuery) =>
      channelQuery.withCount('blacklist', (blacklistQuery) =>
        blacklistQuery.where('blacklistedId', auth.user!.id).as('kickCount')
      )
    );
    await auth.user!.load('invitations', (query) =>
      query.where('status', 'invited').preload('channel')
    );
    return auth.user;
  }
}
