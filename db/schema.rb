# This file is auto-generated from the current state of the database. Instead
# of editing this file, please use the migrations feature of Active Record to
# incrementally modify your database, and then regenerate this schema definition.
#
# Note that this schema.rb definition is the authoritative source for your
# database schema. If you need to create the application database on another
# system, you should be using db:schema:load, not running all the migrations
# from scratch. The latter is a flawed and unsustainable approach (the more migrations
# you'll amass, the slower it'll run and the greater likelihood for issues).
#
# It's strongly recommended that you check this file into your version control system.

ActiveRecord::Schema.define(version: 20160827094728) do

  # These are extensions that must be enabled in order to support this database
  enable_extension "plpgsql"

  create_table "game_players", force: :cascade do |t|
    t.integer  "order"
    t.string   "role"
    t.boolean  "is_candidate"
    t.boolean  "is_candidate_by_special_election"
    t.boolean  "is_nominee_for_chancellor"
    t.boolean  "is_president"
    t.boolean  "is_chancellor"
    t.boolean  "is_dead"
    t.boolean  "is_voting_yes"
    t.boolean  "was_last_chancellor"
    t.boolean  "was_last_president"
    t.boolean  "has_been_investigated"
    t.datetime "created_at",                       null: false
    t.datetime "updated_at",                       null: false
  end

  create_table "games", force: :cascade do |t|
    t.integer  "team_id"
    t.string   "channel_id"
    t.string   "policies_stack",                        array: true
    t.string   "discarded_stack",                       array: true
    t.string   "played_policies",                       array: true
    t.string   "president_choice"
    t.boolean  "chancellor_asks_for_veto"
    t.integer  "turns_without_elections"
    t.string   "current_step"
    t.datetime "created_at",               null: false
    t.datetime "updated_at",               null: false
    t.index ["team_id"], name: "index_games_on_team_id", using: :btree
  end

  create_table "players", force: :cascade do |t|
    t.integer  "team_id"
    t.string   "username"
    t.datetime "created_at", null: false
    t.datetime "updated_at", null: false
    t.index ["team_id"], name: "index_players_on_team_id", using: :btree
  end

  create_table "teams", force: :cascade do |t|
    t.datetime "created_at", null: false
    t.datetime "updated_at", null: false
  end

end
