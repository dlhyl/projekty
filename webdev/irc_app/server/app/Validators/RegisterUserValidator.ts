import type { HttpContextContract } from '@ioc:Adonis/Core/HttpContext';
import { rules, schema } from '@ioc:Adonis/Core/Validator';
import { CustomReporter } from './Reporters/CustomReporter';

export default class RegisterUserValidator {
  constructor(protected ctx: HttpContextContract) {}
  public reporter = CustomReporter;
  /*
   * Define schema to validate the "shape", "type", "formatting" and "integrity" of data.
   *
   * For example:
   * 1. The username must be of data type string. But then also, it should
   *    not contain special characters or numbers.
   *    ```
   *     schema.string({}, [ rules.alpha() ])
   *    ```
   *
   * 2. The email must be of data type string, formatted as a valid
   *    email. But also, not used by any other user.
   *    ```
   *     schema.string({}, [
   *       rules.email(),
   *       rules.unique({ table: 'users', column: 'email' }),
   *     ])
   *    ```
   */
  public schema = schema.create({
    email: schema.string({}, [
      rules.email(),
      rules.unique({ table: 'users', column: 'email' }),
    ]),
    password: schema.string({}, [
      rules.minLength(6),
      rules.confirmed('passwordConfirmation'),
    ]),
    firstname: schema.string({}, [rules.alpha()]),
    lastname: schema.string({}, [rules.alpha()]),
    username: schema.string({}, [
      rules.minLength(5),
      rules.maxLength(20),
      rules.regex(/^[A-Za-z]\w+$/),
      rules.unique({ table: 'users', column: 'username' }),
    ]),
  });

  /**
   * Custom messages for validation failures. You can make use of dot notation `(.)`
   * for targeting nested fields and array expressions `(*)` for targeting all
   * children of an array. For example:
   *
   * {
   *   'profile.username.required': 'Username is required',
   *   'scores.*.number': 'Define scores as valid numbers'
   * }
   *
   */
  public messages = {
    'email.email': 'E-mail not valid',
    'email.unique': 'E-mail already registered',
    'password.minLength': 'Password must have at least 6 characters',
    'password.confirmed': 'Passwords do not match',
    'firstname.alpha': 'Firstname must contain only letters',
    'lastname.alpha': 'Lastname must contain only letters',
    'username.regex':
      'Username must contain only letters, numbers and undescore',
    'username.unique': 'Username is already taken',
    'username.minLength': 'Username must have 5-20 characters',
    'username.maxLength': 'Username must have 5-20 characters',
  };
}
