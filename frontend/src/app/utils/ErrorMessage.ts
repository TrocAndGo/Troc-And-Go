export function errorMessageFromStatusCode(status: number): string | null {
    switch (status) {
        case 0:
            return 'Problème de connexion au serveur';
        case 400:
            return 'Données non valides';
        case 404:
            return 'Ressource introuvable';
        case 500:
            return 'Problème de serveur, merci de rééssayer plus tard';
        default:
            return null;
    }
}
