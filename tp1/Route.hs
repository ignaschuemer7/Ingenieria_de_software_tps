
module Route ( Route, newR, inOrderR, inRouteR)
  where
    
import Data.List (elemIndex)

data Route = Rou [ String ] deriving (Eq, Show)

newR :: [ String ] -> Route                    -- construye una ruta segun una lista de ciudades
newR = Rou

inOrderR :: Route -> String -> String -> Bool  -- indica si la primer ciudad consultada esta antes que la segunda ciudad en la ruta
inOrderR (Rou cities) city1 city2 | idxCity1 == Nothing || idxCity2 == Nothing = False
                                  | idxCity1 <=  idxCity2 = True
                                  | otherwise = False
                                    where
                                      idxCity1 = elemIndex city1 cities
                                      idxCity2 = elemIndex city2 cities

inRouteR :: Route -> String -> Bool -- indica si la ciudad consultada está en la ruta
inRouteR (Rou cities) city = elem city cities