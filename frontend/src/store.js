import { createStore } from 'redux';

const defaultUser = {
    isAuth: false,
}

const reducer = (user = defaultUser, action) => {
    switch (action.type){
        case "SET_IS_AUTH":
            return { ...user, isAuth: action.isAuth}
        default:
            return user
    }
}

const store = createStore(reducer);

export default store;
