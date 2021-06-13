module.exports = function (sequelize, DataTypes) {
  return sequelize.define(
    "znizenie_imania_issues",
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
      corporate_body_name: {
        type: DataTypes.STRING,
        allowNull: false,
      },
      street: {
        type: DataTypes.STRING,
        allowNull: true,
      },
      building_number: {
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
      country: {
        type: DataTypes.STRING,
        allowNull: true,
      },
      br_court_code: {
        type: DataTypes.STRING,
        allowNull: false,
      },
      br_court_name: {
        type: DataTypes.STRING,
        allowNull: false,
      },
      br_section: {
        type: DataTypes.STRING,
        allowNull: false,
      },
      br_insertion: {
        type: DataTypes.STRING,
        allowNull: false,
      },
      cin: {
        type: DataTypes.BIGINT,
        allowNull: false,
      },
      decision_text: {
        type: DataTypes.TEXT,
        allowNull: true,
      },
      decision_date: {
        type: DataTypes.DATEONLY,
        allowNull: true,
      },
      equity_currency_code: {
        type: DataTypes.STRING,
        allowNull: false,
      },
      old_equity_value: {
        type: DataTypes.DECIMAL,
        allowNull: false,
      },
      new_equity_value: {
        type: DataTypes.DECIMAL,
        allowNull: false,
      },
      resolution_store_date: {
        type: DataTypes.DATEONLY,
        allowNull: true,
      },
      first_ov_released_date: {
        type: DataTypes.DATEONLY,
        allowNull: true,
      },
      first_ov_released_number: {
        type: DataTypes.STRING,
        allowNull: true,
      },
      created_at: {
        type: DataTypes.DATE,
        allowNull: false,
      },
      updated_at: {
        type: DataTypes.DATE,
        allowNull: false,
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
      tableName: "znizenie_imania_issues",
      schema: "ov",
      timestamps: false,
      indexes: [
        {
          name: "index_ov.znizenie_imania_issues_on_bulletin_issue_id",
          fields: [{ name: "bulletin_issue_id" }],
        },
        {
          name: "index_ov.znizenie_imania_issues_on_raw_issue_id",
          unique: true,
          fields: [{ name: "raw_issue_id" }],
        },
        {
          name: "index_ov.znizenie_imania_issues_on_updated_at_and_id",
          unique: true,
          fields: [{ name: "updated_at" }, { name: "id" }],
        },
        {
          name: "znizenie_imania_issues_company_id",
          fields: [{ name: "company_id" }],
        },
        {
          name: "znizenie_imania_issues_pkey",
          unique: true,
          fields: [{ name: "id" }],
        },
      ],
    }
  );
};
