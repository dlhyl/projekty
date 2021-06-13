module.exports = function (sequelize, DataTypes) {
  return sequelize.define(
    "or_podanie_issues",
    {
      id: {
        autoIncrement: true,
        type: DataTypes.INTEGER,
        allowNull: false,
        primaryKey: true,
      },
      bulletin_issue_id: {
        type: DataTypes.INTEGER,
        allowNull: false,
        references: {
          model: "bulletin_issues",
          key: "id",
        },
      },
      raw_issue_id: {
        type: DataTypes.INTEGER,
        allowNull: false,
        references: {
          model: "raw_issues",
          key: "id",
        },
      },
      br_mark: {
        type: DataTypes.STRING,
        allowNull: false,
      },
      br_court_code: {
        type: DataTypes.STRING,
        allowNull: false,
      },
      br_court_name: {
        type: DataTypes.STRING,
        allowNull: false,
      },
      kind_code: {
        type: DataTypes.STRING,
        allowNull: false,
      },
      kind_name: {
        type: DataTypes.STRING,
        allowNull: false,
      },
      cin: {
        type: DataTypes.BIGINT,
        allowNull: true,
      },
      registration_date: {
        type: DataTypes.DATEONLY,
        allowNull: true,
      },
      corporate_body_name: {
        type: DataTypes.STRING,
        allowNull: true,
      },
      br_section: {
        type: DataTypes.STRING,
        allowNull: false,
      },
      br_insertion: {
        type: DataTypes.STRING,
        allowNull: false,
      },
      text: {
        type: DataTypes.TEXT,
        allowNull: false,
      },
      created_at: {
        type: DataTypes.DATE,
        allowNull: false,
      },
      updated_at: {
        type: DataTypes.DATE,
        allowNull: false,
      },
      address_line: {
        type: DataTypes.STRING,
        allowNull: true,
      },
      street: {
        type: DataTypes.STRING,
        allowNull: true,
      },
      postal_code: {
        type: DataTypes.STRING,
        allowNull: true,
      },
      city: {
        type: DataTypes.STRING,
        allowNull: true,
      },
      company_id: {
        type: DataTypes.BIGINT,
        allowNull: true,
        references: {
          model: "companies",
          key: "cin",
        },
      },
    },
    {
      sequelize,
      tableName: "or_podanie_issues",
      schema: "ov",
      timestamps: false,
      indexes: [
        {
          name: "index_ov.or_podanie_issues_on_bulletin_issue_id",
          fields: [{ name: "bulletin_issue_id" }],
        },
        {
          name: "index_ov.or_podanie_issues_on_raw_issue_id",
          fields: [{ name: "raw_issue_id" }],
        },
        {
          name: "index_ov.or_podanie_issues_on_updated_at_and_id",
          unique: true,
          fields: [{ name: "updated_at" }, { name: "id" }],
        },
        {
          name: "or_podanie_issues_company_id",
          fields: [{ name: "company_id" }],
        },
        {
          name: "or_podanie_issues_pkey",
          unique: true,
          fields: [{ name: "id" }],
        },
      ],
    }
  );
};
